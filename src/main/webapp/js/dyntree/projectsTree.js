
function getfile_iconIcon(nameFile) {
    var array = nameFile.split(".");
    var extension = array[array.length - 1].toLowerCase();
    var icon = extensionsIcons[extension];
    if (icon == null)
        icon = extensionsIcons[""];
    return icon;
}

function getNodeByFileUri(fileUri) {
    var res = undefined;
    $("#projectsTree").dynatree("getRoot").visit(function (node) {
        var nodeName = node.data.keyPath === "undefined" ? node.data.title : node.data.keyPath;
        if (WorkspaceManager.getSelectedWorkspace() + "/" + nodeName === fileUri) {
            res = node;
            return;
        }
    });
    return res;
}

function getFileUriByNode(node) {
    if (node) {
        var workspaceName = WorkspaceManager.getSelectedWorkspace(),
            parent = node.getParent(),
            fileUri = "";
        while (parent.data.title) {
            fileUri = parent.data.title + "/" + fileUri;
            parent = parent.getParent();
        }
        return workspaceName + "/" + fileUri + node.data.title;
    } else {
        return;
    }
}

// --- Implement Cut/Copy/Paste --------------------------------------------
var originNode = null;
var originKeyPath = "";
var destNode = null;
var pasteMode = null;
function copyPaste(action, node) {
    switch (action) {
        case "cut":
        case "copy":
            pasteMode = action;
            break;
        case "paste":
            if (!originNode) {
                CommandApi.echo("<p style='color:red'>Project Tree: Clipboard is empty.</p>");
                break;
            }
            var origin = WorkspaceManager.getSelectedWorkspace() + "/" + originNode.data.keyPath;
            var dest = WorkspaceManager.getSelectedWorkspace() + "/" + destNode.data.keyPath + "/" + originNode.data.title;
            var originProj = WorkspaceManager.getSelectedWorkspace() + "/" + originNode.data.keyPath.split("/")[0];
            var destProj = WorkspaceManager.getSelectedWorkspace() + "/" + destNode.data.keyPath.split("/")[0];

            var msg = "";
            if (originNode.getLevel() === 1) {
                msg += "Cannot paste a project into a project.";
            } else if (originProj !== destProj) {
                msg += "Cannot paste element into a different project.";
            } else if (!!getNodeByFileUri(dest)) {
                msg += "Element already exists.";
            } else if (!destNode.data.isFolder) {
                msg += "Cannot paste into a file.";
            }

            if (msg !== "") {
                CommandApi.echo("<p style='color:red'>Project Tree: " + msg + "</p>");
                break;
            }

            if (pasteMode == "cut") {
                CommandApi.move(origin, dest);
            } else {
                CommandApi.copy(origin, dest);
                originNode.data.keyPath = originKeyPath;
            }
            getNodeByFileUri(dest).data.keyPath = destNode.data.keyPath + "/" + originNode.data.title;
            pasteMode = null;
            break;
        default:
//		CommandApi.echo("<p style='color:red'>Project Tree: Unhandled clipboard action '"+ action +"'</p>");
    }
}
;

// --- Contextmenu helper --------------------------------------------------
function bindContextMenu(span) {
    // Add context menu to this node:
    $(span).contextMenu({
        menu: "myMenu"
    }, function (action, el, pos) {
        // The event was bound to the <span> tag, but the node object
        // is stored in the parent <li> tag
        var node = $.ui.dynatree.getNode(el);
        switch (action) {
            case "edit":
                var prevTitle = node.data.title,
                    tree = node.tree;
                //get extension if it is not a folder
                var extension = !node.data.isFolder ? "." + node.data.title.split(".")[node.data.title.split(".").length - 1] : "";
                console.log(extension);
                // Disable dynatree mouse- and key handling
                tree.$widget.unbind();
                // Replace node with <input>
                //$(".dynatree-title", node.span).html("<input type='text' tabindex='-1' id='editNode' value='" + prevTitle + "' />");

                $(".dynatree-title", node.span).replaceWith($("<span id='metaA'><input type='text' tabindex='-1' id='editNode' value='" + prevTitle + "' /></span>"));

                // Avoid to reload page when you click in another node
                $(".dynatree-node a").click(function (e) {
                    e.preventDefault();
                    console.log("dynatree-node click");
                    var isInput = $(this).find("input#editNode").length != 0;
                    if (!isInput) {
                        $("input#editNode").blur();
                    }
                });
                $("input#editNode").click(function () {
                    console.log("click input");
                    $(this).focus();
                });

                // Focus <input> and bind keyboard handler
                $("input#editNode").focus().keydown(function (event) {
                    switch (event.which) {
                        case 27: // [esc]
                            // discard changes on [esc]
                            $("input#editNode").val(prevTitle);
                            $(this).blur();
                            break;
                        case 13: // [enter]
                            // simulate blur to accept new value
                            $(this).blur();
                            break;
                    }
                }).blur(function (event) {
                    // Accept new value, when user leaves <input>
                    var title = $("input#editNode").val();
                    if (title.split(".").length < 2) {
                        title = title + extension;
                    }
                    CommandApi.renameNode(WorkspaceManager.getSelectedWorkspace() + "/" + node.data.keyPath, title, function (result) {});
                    // Re-enable mouse and keyboard handlling
                    tree.$widget.bind();
                    node.focus();
                    node.setTitle(title);
                    // Activate current file node
                    if (EditorManager.currentUri && EditorManager.currentUri != "") {
                        getNodeByFileUri(EditorManager.currentUri).activate();
                    } else {
                        node.deactivate();
                    }
                });
                break;
            case "delete":
                var nodeUri = (node.data.keyPath !== "undefined") ? WorkspaceManager.getSelectedWorkspace() + "/" + node.data.keyPath : WorkspaceManager.getSelectedWorkspace() + "/" + node.data.title;
                showModal("Please, confirm your decision", "<strong>Are you sure you want to delete \"" + nodeUri + "\"?</strong>", "Delete", function () {
                    CommandApi.deleteNode(nodeUri, function (result) {
                        hideModal();
                    });
                });
                break;
            case "cut":
                pasteMode = "cut";
                originNode = node;
                console.log(originNode);
                originKeyPath = originNode.data.keyPath;
                console.log("------>>" + originKeyPath);
                break;
            case "copy":
                pasteMode = "copy";
                originNode = node;
                console.log(originNode);
                originKeyPath = originNode.data.keyPath;
                console.log("------>>" + originKeyPath);
                break;
            case "paste":
                destNode = node;
                console.log(destNode);
                copyPaste(action, node);
                break;
            case "upload":
                var tmpNode = currentSelectedNode;
                originNode = node;
                if (!originNode.data.isFolder) {
                    originNode = originNode.getParent();
                }
                originKeyPath = originNode.data.keyPath;
                console.log("Upload------>>" + originKeyPath);
                currentSelectedNode = originNode;
                uploadFileItem.onCancel = function () {
                    currentSelectedNode = tmpNode;
                    hideModal();
                    uploadFileItem.onCancel = function () {
                        hideModal();
                    }
                };
                uploadFileItem.onClick();
                break;
            default:
                alert("Todo: appply action '" + action + "' to node " + node);
                console.log("---->>" + WorkspaceManager.getSelectedWorkspace() + "/" + node.data.keyPath);

        }
    });
}
;

var sortProjectsTree = function (container) {
    var cmp = function(a, b) {
        var ret = null;
        if (a.data.isFolder && b.data.isFolder) {
            a = a.data.title.toLowerCase();
            b = b.data.title.toLowerCase();
            ret = a > b ? 1 : a < b ? -1 : 0;
        } else if (a.data.isFolder && !b.data.isFolder) {
            ret = -1;
        } else if (!a.data.isFolder && b.data.isFolder) {
            ret = 1;
        } else {
            a = a.data.title.toLowerCase();
            b = b.data.title.toLowerCase();
            ret = a > b ? 1 : a < b ? -1 : 0;
        }
        return ret;
    };
    var node = container || $("#projectsTree").dynatree("getTree").getRoot();
    node.sortChildren(cmp, true);
};

$(function () {
    // Attach the dynatree widget to an existing <div id="tree"> element
    // and pass the tree options as an argument to the dynatree() function:
    $("#projectsTree").dynatree({
        title: "Dynatree",
        keyboard: true,
        persist: true,
        selectMode: 3,
        minExpandLevel: 1,
//        rootVisible: false,
        persist : true,
//        onActivate : function(node) {
//            $("#echoActivated").text(
//                    node.data.title + ", key=" + node.data.key);
//        },
        onClick: function (node, event) {
            // Close menu on click
            if ($(".contextMenu:visible").length > 0) {
                $(".contextMenu").hide();
                // return false;
            }
        },
        onKeydown: function (node, event) {
            // Eat keyboard events, when a menu is open
            if ($(".contextMenu:visible").length > 0)
                return false;

            switch (event.which) {

                // Open context menu on [Space] key (simulate right
                // click)
                case 32: // [Space]
                    $(node.span).trigger("mousedown", {
                        preventDefault: true,
                        button: 2
                    }).trigger("mouseup", {
                        preventDefault: true,
                        pageX: node.span.offsetLeft,
                        pageY: node.span.offsetTop,
                        button: 2
                    });
                    return false;
            }
        },
        onCreate: function (node, span) {
            bindContextMenu(span);
            sortProjectsTree();
        },
        /*
         * Load lazy content (to show that context menu will
         * work for new items too)
         */
        onLazyRead: function (node) {
            node.appendAjax({
                url: "sample-data2.json"
            });
        },
        // Drag'n'drop support
//        dnd: {
//            // Make tree nodes draggable:
//            onDragStart: function (node) {
//                console.log("Activated node: " + node);
//                currentSelectedNode = node;
//                var result = true;
//                if (node.lastsib == "dynatree-lastsib" || node.getLevel() === 1) {
//                    result = false;
//                }
//                return result;
//            }, // Callback(sourceNode), return true, to enable
//            // dnd
//            onDragStop: null, // Callback(sourceNode)
//            // Make tree nodes accept draggables
//            autoExpandMS: 500, // Expand nodes after n
//            // milliseconds of hovering.
//            preventVoidMoves: true, // Prevent dropping
//            // nodes 'before self',
//            // etc.
//            //revert : true, // true: slide helper back to source
//            // if drop is rejected
//            onDragEnter: function (node, sourceNode) {
//                var result = true;
//                if (node.isDescendantOf(sourceNode) || !node.data.isFolder || node.lastsib == "dynatree-lastsib") {
//                    result = false;
//                }
//                return result;
//            }, // Callback(targetNode, sourceNode, ui,
//            // draggable)
//            onDragOver: function (node, sourceNode, hitMode) {
//                // Deactivating to drag and drop between projects
//                var Porigin = sourceNode.data.keyPath.substring(0, sourceNode.data.keyPath.indexOf("/"));
//                var Pdest = node.data.keyPath.substring(0, node.data.keyPath.indexOf("/"));
//                if (node.isDescendantOf(sourceNode) || !node.data.isFolder || Porigin !== Pdest) {
//                    return false;
//                }
//                if (!node.data.isFolder && hitMode === "over") {
//                    return "after";
//                }
//            }, // Callback(targetNode,
//            // sourceNode, hitMode)
//            onDrop: function (node, sourceNode, hitMode, ui, draggable) {
//                var Porigin = sourceNode.data.keyPath.substring(0, sourceNode.data.keyPath.indexOf("/"));
//                var Pdest = node.data.keyPath.substring(0, node.data.keyPath.indexOf("/"));
//                if (Pdest == "") {
//                    Pdest = node.data.keyPath;
//                }
//                if (Porigin == Pdest) {
//                    //si esta dentro del proyecto --> move
//                    CommandApi.move(WorkspaceManager.getSelectedWorkspace() + "/" + sourceNode.data.keyPath,
//                        WorkspaceManager.getSelectedWorkspace() + "/" + node.data.keyPath + "/" + sourceNode.data.title);
//                } else {
//                    //en otro proyecto --> copia
//                    CommandApi.copy(WorkspaceManager.getSelectedWorkspace() + "/" + sourceNode.data.keyPath,
//                        WorkspaceManager.getSelectedWorkspace() + "/" + node.data.keyPath + "/" + sourceNode.data.title);
//                }
//            }, // Callback(targetNode, sourceNode, hitMode, ui,
//            // draggable)
//            onDragLeave: function (node, sourceNode) {
//                /**
//                 * Always called if onDragEnter was called.
//                 */
////								logMsg("tree.onDragLeave(%o, %o)", node,
////										sourceNode);
//            } // Callback(targetNode, sourceNode)
//        },
        persist : true,
            children: []

    });

    $("#projectsTree").attr("checked", true) // set state, to prevent caching
        .click(function () {
            var f = $(this).attr("checked");
            if (f) {
                $("#projectsTree").dynatree("option", "fx", {
                    height: "toggle",
                    duration: 25
                });
            } else {
                $("#projectsTree").dynatree("option", "fx", null);
            }
        });
});
