package br.com.cauezito.model;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;
import br.com.cauezito.dao.GenericDao;
import br.com.cauezito.entity.City;
import br.com.cauezito.entity.Curriculum;
import br.com.cauezito.entity.Image;
import br.com.cauezito.entity.Person;
import br.com.cauezito.entity.State;
import br.com.cauezito.entity.Telephone;
import br.com.cauezito.repository.PersonDao;

@Named(value = "personBean")
@SessionScoped
public class PersonBean implements Crud, Serializable {

	private static final long serialVersionUID = 1L;

	private Person person = new Person();

	@Inject
	private GenericDao<Person> dao;

	@Inject
	private PersonDao pdao;

	@Inject
	private EntityManager entityManager;

	private List<SelectItem> states;
	private List<SelectItem> cities;
	private List<String> skills = new ArrayList<String>();
	private List<String> relationship = new ArrayList<String>();
	private UploadedFile photo;
	private UploadedFile curric;
	private List<String> phones = new ArrayList<String>();
	private Telephone telephone;
	private Image image;
	private Curriculum curriculum;
	
	public PersonBean() {
		this.skills();
		this.relationship();		
	}

	public String recoverInfoUser() {
		this.getSession();
		return "updateInfoUser.xhtml";
	}

	@Override
	public String save() {
		if (photo.getSize() != 0) {
			image = new Image();
			image.savePhoto(photo);
			image.setPerson(person);
			person.setImage(image);
		}
		
		if(curric.getSize() != 0) {
			curriculum = new Curriculum();
			curriculum.saveCurriculum(curric);
			curriculum.setPerson(person);
			person.setCurriculum(curriculum);
		}

		if (phones != null && !phones.isEmpty()) {
			List<Telephone> p = new ArrayList<Telephone>();
			for (String phone : phones) {
				telephone = new Telephone();
				telephone.setNumber(phone);
				telephone.setPerson(person);
				p.add(telephone);				
			}
			person.setPhones(p);
		} else {
			person.getPhones().clear();
		}

		if (dao.merge(person) != null) {
			this.setSession("personOn", person);
			this.showMessage("Informações atualizadas!");
		} else {
			this.showMessage("Não foi possível atualizar as informações!");
		}
		return "";
	}

	private void showMessage(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	@Override
	public String removeById() {
		dao.removeById(person);
		person = new Person();
		this.showMessage("Usuário removido com sucesso!");
		return "";
	}

	@Override
	public String remove() {
		return null;
	}

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		ec.getSessionMap().remove("personOn");
		HttpServletRequest req = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
		req.getSession().invalidate();
		this.showMessage("Você saiu");
		return "login";
	}
	
	@Override
	public String clear() {
		person = new Person();
		return "";
	}

	public String login() {
		Person p = pdao.findUser(person.getLogin(), person.getPassword());

		if (p != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext ec = context.getExternalContext();
			ec.getSessionMap().put("personOn", p);
			this.getSession();

			return "home.xhtml?faces-redirect=true";
		} 

		return "login.xhtml";
	}

	private void getSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		person = (Person) ec.getSessionMap().get("personOn");
		
		if(person.getPhones() != null) {
			phones.clear();
			for (Telephone phone : person.getPhones()) {
				this.phones.add(phone.getNumber());
			}
		}	
	}

	private void setSession(String key, Person person) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ec = context.getExternalContext();
		ec.getSessionMap().remove(key);
		ec.getSessionMap().put(key, person);
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
	
	private void relationship() {
		relationship.add("Solteiro(a)");
		relationship.add("Casado(a)");
		relationship.add("Divorciado(a)");
		relationship.add("Viúvo(a)");
		relationship.add("Outro");
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public GenericDao<Person> getGenericDao() {
		return dao;
	}

	public void setGenericDao(GenericDao<Person> dao) {
		this.dao = dao;
	}

	public List<SelectItem> getStates() {
		states = pdao.allStates();
		return states;
	}

	public List<SelectItem> getCities() {
		return cities;
	}

	public void setCities(List<SelectItem> cities) {
		this.cities = cities;
	}

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
		this.skills = skills;
	}
	
	public List<String> getRelationship() {
		return relationship;
	}

	public void setRelationship(List<String> relationship) {
		this.relationship = relationship;
	}

	public UploadedFile getPhoto() {
		return photo;
	}

	public void setPhoto(UploadedFile photo) {
		this.photo = photo;
	}

	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	public UploadedFile getCurriculum() {
		return curric;
	}

	public void setCurric(UploadedFile curric) {
		this.curric = curric;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public UploadedFile getCurric() {
		return curric;
	}

	@Override
	public void listAll() {
		// TODO Auto-generated method stub
		
	}

}
