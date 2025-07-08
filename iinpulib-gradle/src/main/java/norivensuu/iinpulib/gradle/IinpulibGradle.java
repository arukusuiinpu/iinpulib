package norivensuu.iinpulib.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class IinpulibGradle implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getLogger().lifecycle("✅ IinpulibGradle applied!");
    }
}
