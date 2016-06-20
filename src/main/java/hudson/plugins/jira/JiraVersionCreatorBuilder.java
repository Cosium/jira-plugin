package hudson.plugins.jira;

import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * A build step which creates new JIRA version. It has the same functionality as
 * {@link JiraVersionCreator} but can be used multiple times in the same build
 * (e.g. for different projects) and supports conditional triggering.
 *
 * @author marcin.czerwinski marcinczrw@gmail.com
 */
public class JiraVersionCreatorBuilder extends Builder implements SimpleBuildStep {

	private String jiraVersion;
	private String jiraProjectKey;

	@DataBoundConstructor
	public JiraVersionCreatorBuilder(String jiraVersion, String jiraProjectKey) {
		this.jiraVersion = jiraVersion;
		this.jiraProjectKey = jiraProjectKey;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	public String getJiraVersion() {
		return jiraVersion;
	}

	public void setJiraVersion(String jiraVersion) {
		this.jiraVersion = jiraVersion;
	}

	public String getJiraProjectKey() {
		return jiraProjectKey;
	}

	public void setJiraProjectKey(String jiraProjectKey) {
		this.jiraProjectKey = jiraProjectKey;
	}

	@Override
	public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		JiraSite site = getSiteForProject(build.getParent());
		VersionCreator.perform(site, jiraVersion, jiraProjectKey, build, listener);
	}

	JiraSite getSiteForProject(Job<?, ?> project) {
		return JiraSite.get(project);
	}

	@Override
	public BuildStepDescriptor<Builder> getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

		public DescriptorImpl() {
			super(JiraVersionCreatorBuilder.class);
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public JiraVersionCreatorBuilder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
			return req.bindJSON(JiraVersionCreatorBuilder.class, formData);
		}

		@Override
		public String getDisplayName() {
			return Messages.JiraVersionCreatorBuilder_DisplayName();
		}

		@Override
		public String getHelpFile() {
			return "/plugin/jira/help-version-create.html";
		}
	}
}
