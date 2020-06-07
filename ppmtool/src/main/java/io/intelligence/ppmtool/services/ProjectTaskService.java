package io.intelligence.ppmtool.services;

import io.intelligence.ppmtool.domain.Backlog;
import io.intelligence.ppmtool.domain.Project;
import io.intelligence.ppmtool.domain.ProjectTask;
import io.intelligence.ppmtool.exceptions.ProjectNotFoundException;
import io.intelligence.ppmtool.repositories.BacklogRepository;
import io.intelligence.ppmtool.repositories.ProjectRepository;
import io.intelligence.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;
    @Autowired
    private ProjectTaskRepository projectTaskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {

            //PTs to be added to a specific project, project != null,  BL exists
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();//backlogRepository.findByProjectIdentifier(projectIdentifier);

            //set the bl to pt
            projectTask.setBacklog(backlog);
            //we want our project sequence to be like this IDPRO-1 IDPRO-2
            Integer backlogSequence = backlog.getPTSequence();
            //Update the BL SEQUENCE
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);

            //Add sequence to Project Task
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //INITIAL priority when priority null
            if(projectTask.getPriority()==null||projectTask.getPriority()==0) {
                projectTask.setPriority(3);
            }

            //INITIAL status when status null
            if(StringUtils.isEmpty(projectTask.getStatus())) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);

    }

    public Iterable<ProjectTask> findBacklogById(String id, String username) {

        projectService.findProjectByIdentifier(id, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence (String backlog_id, String pt_id, String username) {

        //make sure we are searching on the right backlog
        projectService.findProjectByIdentifier(backlog_id, username);

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask==null) {
            throw new ProjectNotFoundException("Project Task: '" + pt_id + "' not found");
        }
        else
        {
            if(!projectTask.getProjectIdentifier().equals(backlog_id))
                throw new ProjectNotFoundException("Project Task: '" + pt_id + "' does not exist in project'" + backlog_id + "'");
        }

        //make sure that the backlog/project id in the path corresponds to the right program

        return projectTask;
    }

    public ProjectTask updateByProjectSequence (ProjectTask updatedTask, String backlog_id, String pt_id, String username) {

        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence (String backlog_id, String pt_id, String username) {

        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
        projectTaskRepository.delete(projectTask);
    }
}
