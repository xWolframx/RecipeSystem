package com.example.myapplication;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/* Класс руцепта для хранения данных о рецепте
 * и дальнейшей обработки его данных
 */
public class Recipe {
	private long id_recipe;
	private String descriptions;
	private Date date_create;
	private Date term;
	private long id_doctor;
	private long id_patient;
	private long id_priority;
	
	public Recipe() {
	}
	
	public Recipe(long id_pecipe, String description, Date date_create,
			Date term, long id_doctor, long id_patient, long id_priority) {
		
		this.id_recipe = id_pecipe;
		this.descriptions = description;
		this.date_create = date_create;
		this.term = term;
		this.id_doctor = id_doctor;
		this.id_patient = id_patient;
		this.id_priority =id_priority;
	}
	
	public void setIdRecipe(long id_recipe) {
		this.id_recipe = id_recipe;
	}
	
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setDateCreate(Date date_create) {
		this.date_create = date_create;
	}
	
	public void setTerm(Date term) {
		this.term = term;
	}
	
	public void setIdDoctor(long id_doctor) {
		this.id_doctor = id_doctor;
	}
	
	public void setIdPatient(long id_patient) {
		this.id_patient = id_patient;
	}
	
	public void setIdPriority(long id_priority) {
		this.id_priority = id_priority;
	}
	
	public long getIdRecipe() {
		return this.id_recipe;
	}
	
	public String getDescriptions() {
		return this.descriptions;
	}
	
	public Date getDateCreate() {
		return this.date_create;
	}
	
	public String getDateCreateLD() {
		LocalDate localDate = this.date_create.toLocalDate();
		return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	}
	
	public Date getTerm() {
		return this.term;
	}
	
	public String getTermLD() {
		LocalDate localDate = this.term.toLocalDate();
		return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	}
	
	public long getIdDoctor() {
		return this.id_doctor;
	}
	
	public long getIdPatient() {
		return this.id_patient;
	}
	
	public long getIdPriority() {
		return this.id_priority;
	}
	
	public String getFIOPatient() {
		DAO.loadDriverDB();
		PatientDAO patientDAO = new PatientDAO();
		Patient patient = patientDAO.getPatient(this.id_patient);
		return patient.getName() + " "
				+ patient.getSurname() + " " 
				+ patient.getMiddleName();
	}
	
	public String getFIODoctor() {
		DAO.loadDriverDB();
		DoctorDAO doctorDAO = new DoctorDAO();
		Doctor doctor = doctorDAO.getDoctor(this.id_doctor);
		return doctor.getName() + " "
				+ doctor.getSurname() + " "
				+doctor.getMiddleName();
	}
	
	public String getNamePriority() {
		DAO.loadDriverDB();
		PriorityDAO priorityDAO = new PriorityDAO();
		Priority priority = priorityDAO.getPriority(this.id_priority);
		return priority.getName();
	}

}
