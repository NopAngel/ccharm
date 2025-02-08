import java.util.ArrayList;
import java.util.List;

public class ProjectManager {
    private List<Project> projects;

    public ProjectManager() {
        projects = new ArrayList<>();
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() {
        return projects;
    }
}
