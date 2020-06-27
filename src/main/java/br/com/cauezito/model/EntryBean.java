package br.com.cauezito.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import br.com.cauezito.dao.GenericDao;
import br.com.cauezito.entity.Entry;
import br.com.cauezito.entity.Person;
import br.com.cauezito.repository.EntryDao;
import br.com.cauezito.repository.EntryDaoImpl;

@ViewScoped
@ManagedBean(name = "entryBean")
public class EntryBean implements Crud{
	private Entry entry = new Entry();
	private GenericDao<Entry> dao = new GenericDao<Entry>();
	private List<Entry> entries = new ArrayList<Entry>();
	private EntryDao entryDao = new EntryDaoImpl();
	
	private Person getUserOn() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext ex = context.getExternalContext();
		Person p = (Person) ex.getSessionMap().get("personOn");
		return p;
	}
	
	@Override
	public String save() {
		Person p = this.getUserOn();
		entry.setUser(p);
		dao.save(entry);
		return "";
	}
	@Override
	public String removeById() {
		dao.removeById(entry);
		this.clear();
		this.listAll();
		return "";
	}
	@Override
	public String remove() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@PostConstruct
	@Override
	public void listAll() {
		Person p = this.getUserOn();
		entries = entryDao.list(p.getId());
	}

	@Override
	public String clear() {
		entry = new Entry();
		return "";
	}
	public Entry getEntry() {
		return entry;
	}
	public void setEntry(Entry entry) {
		this.entry = entry;
	}
	public GenericDao<Entry> getDao() {
		return dao;
	}
	public void setDao(GenericDao<Entry> dao) {
		this.dao = dao;
	}
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	
}