package com.example.myapplication;

//Класс содержащий статистику по определённому врачу
public class StatisticsDoctor {
	private String FIODoctor;
	//Общие количесво пациентов
	private int countPatient;
	//Количество лечущихся в данный момент пациентов
	private int countPatientTreated;
	//Количество вылеченных пациентов
	private int countPatientEndTreated;
	
	public StatisticsDoctor() {}
	
	public StatisticsDoctor(String name, String surname, String middle_name,
			int countPatient, int countPatientTreated, int countPatientEndTreated) {
		this.FIODoctor = name + " " + surname + " " + middle_name;
		this.countPatient = countPatient;
		this.countPatientTreated = countPatientTreated;
		this.countPatientEndTreated = countPatientEndTreated;
	}
	
	public void setFIO(String name, String surname, String middle_name) {
		this.FIODoctor = name + " " + surname + " " + middle_name;
	}
	
	public void setCountPatient(int countPatient) {
		this.countPatient = countPatient;
	}
	
	public void setCountPatientTreated(int countPatientTreated) {
		this.countPatientTreated = countPatientTreated;
	}
	
	public void setCountPatientEndTreated(int countPatientEndTreated) {
		this.countPatientEndTreated = countPatientEndTreated;
	}
	
	public String getFIO() {
		return this.FIODoctor;
	}
	
	public int getCountPatient() {
		return this.countPatient;
	}
	
	public int getCountPatientTreated() {
		return this.countPatientTreated;
	}
	
	public int getCountPatientEndTreated() {
		return this.countPatientEndTreated;
	}
}
