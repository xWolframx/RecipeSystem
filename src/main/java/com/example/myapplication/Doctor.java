package com.example.myapplication;

/* Класс доктора для хранения данных о докторе
*  и дальнейшей обработки его данных
*/
public class Doctor {
	private long id_doctor;
	private String name;
	private String surname;
	private String middle_name;
	private String specialty;
	
	public Doctor() {	
	}
	
	public Doctor(long id_doctor, String name, String surname, String middle_name, String specialty) {
		this.id_doctor = id_doctor;
		this.name = name;
		this.surname = surname;
		this.middle_name = middle_name;
		this.specialty = specialty;
	}
	
	public void setIdDoctor(long id_doctor) {
		this.id_doctor = id_doctor;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public void setMiddleName(String middle_name) {
		this.middle_name = middle_name;
	}
	
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	
	public long getIdDoctor() {
		return this.id_doctor;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public String getMiddleName() {
		return this.middle_name;
	}
	
	public String getSpecialty() {
		return this.specialty;
	}
	
	public String getFIODoctor() {
		return this.name + " "
				+ this.surname + " "
				+ this.middle_name;
	}
}
