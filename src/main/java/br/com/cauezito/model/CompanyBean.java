package br.com.cauezito.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.weld.context.RequestContext;

import br.com.cauezito.dao.GenericDao;
import br.com.cauezito.entity.Company;
import br.com.cauezito.entity.JobOpportunity;
import br.com.cauezito.entity.Person;
import br.com.cauezito.repository.CompanyDao;
import br.com.cauezito.util.ShowMessages;

@Named(value = "companyBean")
@SessionScoped
public class CompanyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Company company;

	@Inject
	private GenericDao<Company> companyGenericDao;

	private String aux;

	@Inject
	private JobOpportunity job;

	private List<JobOpportunity> jobs = new ArrayList<JobOpportunity>();

	private List<String> skills = new ArrayList<String>();

	@Inject
	private CompanyDao cdao;

	public CompanyBean() {
		this.skills();
	}

	public String save() {		
		
		if (job != null) {
			job.setSkills(skills);
			job.setCompany(company);
			jobs.add(job);
			company.setJobs(jobs);
		}
		
		if (companyGenericDao.merge(company) != null) {
			this.setSession("companyOn", company);
			HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String page = req.getRequestURI().split("/")[3];
			if(page.contains("updateInfoCompany")) {
				ShowMessages.showMessageInfo("Informações atualizadas com sucesso!");
			} else {			
				ShowMessages.showMessageInfo("Vaga cadastrada!");
				job = new JobOpportunity();
			}
			skills.clear();	
		} else {
			ShowMessages.showMessageError("Aconteceu um erro. Tente novamente.");
		}
		return "";
	}

	public String login() {
		Company c = cdao.findCompany(company.getEmail(), company.getPassword(), company.getCnpj());

		if (c != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext ec = context.getExternalContext();
			ec.getSessionMap().put("companyOn", c);
			this.getSession();

			return "company/company.xhtml?faces-redirect=true";
		}
		return "login.xhtml";
	}

	public String updateAccount() {

		if (company.getPassword().equals(this.aux)) {

			if (companyGenericDao.merge(company) != null) {
				ShowMessages.showMessageInfo("Informações atualizadas");
				
			}
		} else {
			ShowMessages.showMessageError("As senhas não conferem!");
		}
		
		this.aux = "";

		return "";
	}

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		ec.getSessionMap().remove("companyOn");
		HttpServletRequest req = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		req.getSession().invalidate();
		ShowMessages.showMessageInfo("Você saiu");
		return "/login.xhtml?faces-redirect=true";
	}

	public String recoverInfoCompany() {
		this.getSession();
		return "/company/updateInfoCompany.xhtml?faces-redirect=true";
	}

	public String newJob() {
		job = new JobOpportunity();
		skills.clear();
		return "/company/newJob.xhtml?faces-redirect=true";
	}

	private void getSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		company = (Company) ec.getSessionMap().get("companyOn");

		if (company.getJobs() != null && !company.getJobs().isEmpty()) {
			jobs.clear();
			for (JobOpportunity job : company.getJobs()) {
				this.jobs.add(job);
			}
		}
	}

	private void setSession(String key, Company company) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		ec.getSessionMap().remove(key);
		ec.getSessionMap().put(key, company);
	}

	private void skills() {
		skills.add("PHP");
		skills.add("Java");
		skills.add("MySQL");
		skills.add("Oracle");
		skills.add("PostgreSQL");
		skills.add("UML");
		skills.add("Go");
		skills.add("Python");
		skills.add("iReport");
		skills.add("CSS");
		skills.add("Javascript");
		skills.add("NodeJs");
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public JobOpportunity getJob() {
		return job;
	}

	public void setJob(JobOpportunity job) {
		this.job = job;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}

	public List<JobOpportunity> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobOpportunity> jobs) {
		this.jobs = jobs;
	}

	public String getAux() {
		return aux;
	}

	public void setAux(String aux) {
		this.aux = aux;
	}

}
