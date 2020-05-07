package es.us.isa.ideas.app.controllers;

import static es.us.isa.ideas.app.controllers.FileController.initRepoLab;
import es.us.isa.ideas.app.entities.Tag;
import es.us.isa.ideas.app.entities.Workspace;
import es.us.isa.ideas.app.repositories.ResearcherRepository;
import es.us.isa.ideas.app.repositories.WorkspaceRepository;
import es.us.isa.ideas.app.security.LoginService;
import es.us.isa.ideas.app.services.GDriveService;
import es.us.isa.ideas.app.services.ResearcherService;
import es.us.isa.ideas.app.services.TagService;
import es.us.isa.ideas.app.services.WorkspaceService;
import es.us.isa.ideas.repo.Facade;
import es.us.isa.ideas.repo.IdeasRepo;
import es.us.isa.ideas.repo.exception.AuthenticationException;
import es.us.isa.ideas.repo.exception.BadUriException;
import es.us.isa.ideas.repo.impl.fs.FSWorkspace;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.services.drive.Drive;

@Controller
@RequestMapping("/workspaces")
public class WorkspaceController extends AbstractController {

    @Autowired
    TagService tagService;

    @Autowired
    WorkspaceService workspaceService;
    
    @Autowired
    ResearcherService researcherService;
    
    @Autowired
    GDriveService gdriveService;
    
    private static final Logger logger = Logger.getLogger(WorkspaceController.class.getName());
    
    private static final String DEMO_MASTER="DemoMaster";
    private static final String SAMPLE_WORKSPACE="SampleWorkspace";

    protected WorkspaceRepository workspaceRepository;
    protected ResearcherRepository researcherRepository;
    
    /* API REST JSON FROM DB */
        
    @RequestMapping(value = "", 
                    method = RequestMethod.GET, 
                    produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Collection<Workspace> getWorkspacesJSON() {
        initRepoLab();
        Collection<Workspace> wsc = workspaceService.findAll();
        return wsc;
    }
    
    @RequestMapping(value = "/{workspaceName}", 
                    method = RequestMethod.GET, 
                    produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Workspace getWorkspaceJSON(@PathVariable("workspaceName") String workspaceName) {
        initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        Workspace ws = workspaceService.findByNameOfPrincipal(workspaceName);
        return ws;
    }
    
    @RequestMapping(value = "/{workspaceName}/tags", 
                    method = RequestMethod.GET, 
                    produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Collection<Tag> getTaggedWorkspacesJSON(@PathVariable("workspaceName") String workspaceName) {
        Collection<Tag> list = tagService.findByWorkspaceName(workspaceName, LoginService.getPrincipal().getUsername());
        return list;
    }

    @RequestMapping(value = "", 
                    method = RequestMethod.POST,
                    consumes = {"text/plain","application/json"})
    @ResponseBody
    public void saveWorkspaceJSON(@RequestParam("workspaceName") String workspaceName, 
                                  @RequestBody(required=true) String workspaceJSON) {
        initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
            workspaceJSON = new String(workspaceJSON.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        String username = LoginService.getPrincipal().getUsername();
        System.out.println("Persisting selected workspace:  " + workspaceName + ", username: " + username);
        boolean success = Boolean.TRUE;
        try {
        	
            Facade.saveSelectedWorkspace(workspaceName, username);
        	
        	
        	
        } catch (Exception e) {
            success = Boolean.FALSE;
        }
        if (success) {
            workspaceService.saveOrUpdate(workspaceJSON);           
        }
    }
    
    @RequestMapping(value = "/{workspaceName}", method = RequestMethod.PUT, consumes = "text/plain")
    @ResponseBody 
    public void updateWorkspaceJSON(@PathVariable("workspaceName") String workspaceName, 
                                    @RequestBody(required=true) String workspaceJSON) {
        initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
            workspaceJSON = new String(workspaceJSON.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        boolean success = Boolean.TRUE;

        try {
            Facade.saveSelectedWorkspace(workspaceName, LoginService.getPrincipal().getUsername());
        } catch (Exception e) {
            success = Boolean.FALSE;
        }
        if (success) {
            workspaceService.saveOrUpdate(workspaceJSON);
        }
    }

    @RequestMapping(value = "/{workspaceName}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteWorkspaceJSON(@PathVariable("workspaceName") String workspaceName) {
        Object o=initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        String username = LoginService.getPrincipal().getUsername();
        boolean success = Boolean.TRUE;

        try {
        	if(o instanceof IdeasRepo) {
            Facade.deleteWorkspace(workspaceName, username);
        	}/*
        	if(o instanceof Drive) {
        	Facade.deleteGDriveWorkspace(workspaceName, username);
        	}*/
        } catch (AuthenticationException | BadUriException e) {
            success = Boolean.FALSE;
        }
        if (success) {
            workspaceService.delete(workspaceName,username);
        }
    }
    
    /*END API REST JSON FROM DB*/ 
    
    //Editor 
    
    @RequestMapping(value = "/{workspaceName}/load", method = RequestMethod.GET)
    @ResponseBody
    public String loadWorkspace(@PathVariable("workspaceName") String workspaceName) {
        logger.log(Level.INFO, "Reading workspace: {0}", workspaceName);
        Object o=initRepoLab();
        
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        String wsJson = "";
        try {
            String username = LoginService.getPrincipal().getUsername();
            if (workspaceName.equalsIgnoreCase(SAMPLE_WORKSPACE) && !Facade.getWorkspaces(username).contains(workspaceName) || "[]".equals(Facade.getWorkspaces(username))) {
             //Si no existe el workspace lo carga directamente en local
            	
                Facade.saveSelectedWorkspace(SAMPLE_WORKSPACE, username);
            	
            }
            //Si no existe en local buscar en google drive
            if(FileController.existeWorkspaceLocal(workspaceName)) {
            wsJson = Facade.getWorkspaceTree(workspaceName, LoginService.getPrincipal().getUsername());
            }else {
            wsJson = Facade.getGDriveWorkspaceTree(workspaceName, LoginService.getPrincipal().getUsername(), gdriveService.getCredentials(username));
            }
            if (LoginService.getPrincipal().getUsername().startsWith("demo")) {
                workspaceService.updateLaunches(workspaceName, "DemoMaster");
            }
            //Si no esta ni en google drive ni en local entonces lanzar la excepcion
        } catch (Exception e) {
            logger.log(Level.WARNING, "Workspace {0} does not exist.", workspaceName);
            return wsJson;
        }
       
        return wsJson;
    }

    /* DEMO */
    
    @RequestMapping(value = "/{workspaceName}/toDemo", method = RequestMethod.GET)
    @ResponseBody
    public String cloneSelectedWorkspaceToDemo(@PathVariable("workspaceName") String workspaceName) {
        
        initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        String res = "";
        System.out.println("URI: "+workspaceName);
        
        String username = LoginService.getPrincipal().getUsername();
        FSWorkspace userWS = new FSWorkspace(workspaceName, username);
        FSWorkspace demoWS = new FSWorkspace(workspaceName, DEMO_MASTER);
                
        Workspace ws = null;
        Workspace demo = null;
                
        boolean demoExists = Boolean.TRUE;
        
        try {
            demoExists = Facade.getWorkspaces(DEMO_MASTER).contains("\"" + workspaceName + "\"");
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
            demoExists = Boolean.FALSE;
        }
        
        if (!demoExists) {
            boolean success = Boolean.TRUE;
            try {
                IdeasRepo.get().getRepo().move(userWS, demoWS, true);
            }
            catch (AuthenticationException e) {
                success = Boolean.FALSE;
                res = "[ERROR] Error creating WS for DemoMaster from " + username;
                logger.log(Level.SEVERE, res, e);
            }
            if(success){
                ws = workspaceService.findByNameOfPrincipal(workspaceName);
                demo = new Workspace();

                if(ws!=null){                 
                    String demomaster = DEMO_MASTER;
                    workspaceService.createWorkspace(workspaceName, demomaster, String.valueOf(ws.getId()));
                }
            }
        } else {
        	res = "Cannot convert workspace, because \""+ workspaceName + "\" already exists.";
                logger.log(Level.INFO, res);
        }
        
        return res;
    }
    
    @RequestMapping(value = "/{workspaceName}/deleteDemo", method = RequestMethod.GET)
    @ResponseBody
    public String deleteSelectedWorkspaceDemo(@PathVariable("workspaceName") String workspaceName) {
        
        initRepoLab();
        try {
            workspaceName = new String(workspaceName.getBytes("iso-8859-15"),"UTF-8");
        } catch (Exception ex) {
            logger.log(Level.INFO, "Unsopported encoding", ex);
        }
        String res = "";
        logger.log(Level.INFO, "URI: {0}", workspaceName);
        
        String username = LoginService.getPrincipal().getUsername();
        FSWorkspace demoWS = new FSWorkspace(workspaceName, DEMO_MASTER);
                        
        boolean demoExists = true;
        try {
            demoExists = Facade.getWorkspaces(DEMO_MASTER).contains("\"" + workspaceName + "\"");
        } 
        catch (Exception e) {
            logger.log(Level.SEVERE, res,e);
            demoExists = false;
        }
        if(demoExists){
            boolean success = Boolean.TRUE;
            try {
                IdeasRepo.get().getRepo().delete(demoWS);
            }
            catch (AuthenticationException e) {
                success = Boolean.FALSE;
                res = "[ERROR] Error deleting WS for DemoMaster from " + username;
                logger.log(Level.SEVERE, res, e);
            }
            if(success){
                workspaceService.delete(workspaceName, DEMO_MASTER);          
            }
        }else{
        	res = "[INFO] The ws doesn't exist.";
                logger.log(Level.INFO, res);
        }
        
        return res;
    }
    
    @RequestMapping("/upload")
    public RedirectView uploadWorkspace(@RequestParam("workspaceName") String workspaceName) {
    	RedirectView res=null;
    	String username = LoginService.getPrincipal().getUsername();
    	Drive credentials;
		try {
			credentials = gdriveService.getCredentials(username);
			boolean test=false;
			Facade.uploadWorkspace(workspaceName, username, credentials,test);
			res=new RedirectView("../app/editor");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
    
    }
    @RequestMapping("/download")
    public RedirectView downloadWorkspace(@RequestParam("workspaceName") String workspaceName) {
    	RedirectView res=null;
    	String username = LoginService.getPrincipal().getUsername();
    	Drive credentials;
		try {
			res=new RedirectView("../app/editor");
			credentials = gdriveService.getCredentials(username);
			Facade.downloadGDriveWorkspace(workspaceName, username, credentials);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		return res;
    }
    	
    
    
}
