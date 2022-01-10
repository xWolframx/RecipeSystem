package com.example.myapplication;

/*
 * Класс для хранения данных о пациенте
 * и дальнейшей их обработки
 */
public class Patient {
	private long id_patient;
	private String name;
	private String surname;
	private String middle_name;
	private String phone_number;
	
	public Patient() {	
	}
	
	public Patient(long id_patient) {
		this.id_patient = id_patient;
	}
	
	public Patient(long id_patient, String name, String surname, String middle_name, String phone_number) {
		this.id_patient = id_patient;
		this.name = name;
		this.surname = surname;
		this.middle_name = middle_name;
		this.phone_number = phone_number;
	}
	
	public void setIdPatient(long id_patient) {
		this.id_patient = id_patient;
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
	
	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}
	
	public long getIdPatient() {
		return this.id_patient;
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
	
	public String getFIOPatient() {
		return this.name + " "
				+ this.surname + " "
				+ this.middle_name;
	}
	
	public String getPhoneNumber() {
		return this.phone_number;
	}
}
