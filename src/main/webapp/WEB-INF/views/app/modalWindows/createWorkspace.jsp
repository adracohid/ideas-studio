<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="ideas" tagdir="/WEB-INF/tags/"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<ideas:app-modal-dialog>
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h4 class="modal-title">
			<spring:message code="editor.actions.modal.create_workspace.title" />
		</h4>
	</div>

	<div class="modal-body">
		<div class="input-group" id="modalCreationField">
			<span class="input-group-addon"><spring:message
					code="editor.actions.modal.create_workspace.msg" /></span> <input
				type="text" class="modalCreationField form-control focusedInput">
		</div>
		<br>
		<div class="input-group" id="descriptionInput">
			<span class="input-group-addon"><spring:message
					code="editor.actions.modal.create_workspace.description" /></span>
			<textarea type="text" class="form-control"></textarea>
		</div>
		<br>
		<div class="input-group" id="tagsInput">
			<span class="input-group-addon"><spring:message
					code="editor.actions.modal.create_workspace.tags" /></span>
			<textarea type="text" class="form-control"></textarea>
		</div>
		<!-- 
		<div class="input-group" id="typeInput" >
            <span class="input-group-addon"><spring:message code="editor.actions.modal.create_workspace.type" /></span>
            
            <input type="radio" class="form-control" id="Google_Drive" name="storage" value="Google_Drive">
            <label for="Google_Drive">Google Drive</label><br>
            <input type="radio" class="form-control" name="storage" id="local" name="storage" value="local">
            <label for="local">Local</label>
            
        </div>
         -->
		<br>
		<div class="input-group" id="typeInput">
			<span class="input-group-addon">Tipo de almacenamiento</span>
			<div class="form-control">
	
			<c:if test="${isgdriveconnected}" >
			
				<label class="radio-inline"><input type="radio"
					id="Google_Drive" name="storage" value="Google_Drive">
					Google Drive </label>
				</c:if>
					 <label class="radio-inline"><input
					type="radio" name="storage" id="local" value="local" checked> Local
				</label>
			</div>
			<!-- TODO ADD TYPE OF WORKSPACE HERE! -->
		</div>
		<div class="input-group">
			<div class="checkbox">
				<label> <input class="btn btn-primary" type="checkbox"
					data-toggle="collapse" data-target="#zipFileDiv"
					aria-expanded="false" aria-controls="zipFileDiv"> <spring:message
						code="editor.actions.modal.initialize_with_zip_file.msg" />
				</label>
			</div>
			<div class="collapse" id="zipFileDiv">
				<div class="input-group">
					<form action="files/uploadAndExtract/" method="POST"
						enctype="multipart/form-data">
						<input type="file" name="zipFile" id="zipFile"
							class="form-control" /> <input type="submit" id="uploadSubmit"
							class="hidden" />
					</form>
				</div>
			</div>
		</div>
	</div>

	<div class="modal-footer">
		<a data-dismiss="modal" class="btn dismiss">Close</a> <a
			class="btn btn-primary continue"> <spring:message
				code="editor.actions.modal.create_workspace.button" />
		</a>
	</div>
</ideas:app-modal-dialog>