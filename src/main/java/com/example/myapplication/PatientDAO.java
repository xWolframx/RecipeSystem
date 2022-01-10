package com.example.myapplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*
 * Класс для доступа к данным,
 * находящимся в таблице Patients
 */
public class PatientDAO extends DAO<Patient> {
	
	public PatientDAO() {
		connetctionToDB(); 
	}
	
	//Получение списка пациентов
	@Override
	public List<Patient> getAll() throws SQLException {
		this.statement = getConnection().createStatement();
		String sql = "SELECT * FROM patients";
		ResultSet patientsResultSet = getStatement().executeQuery(sql);
		List<Patient> listPatients = new ArrayList<Patient>();
		while (patientsResultSet.next()) {
			listPatients.add(new Patient(
					 patientsResultSet.getLong("id_patient")
					,patientsResultSet.getString("name")
					,patientsResultSet.getString("surname")
					,patientsResultSet.getString("middle_name")
					,patientsResultSet.getString("phone_number")
			));
		}
		
		return listPatients;
	}
	
	@Override
	public boolean delete(long id) {
		try {
			this.preparedStatement = getConnection().prepareStatement("DELETE FROM patients WHERE id_patient = ?");
			getPreparedStatement().setLong(1, id);
			getPreparedStatement().execute();
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция добавления нового пациента
	 * получает в кчестве параметра экземпляр класса Patient
	 * возвращает true если добавление произошло успешно,
	 * инчае false
	 */
	@Override
	public boolean insert(Patient patient) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("INSERT INTO patients(name, surname, middle_name, phone_number) "
							+ "VALUES (?, ?, ?, ?)");
			
			this.preparedStatement.setString(1, patient.getName());
			this.preparedStatement.setString(2, patient.getSurname());
			this.preparedStatement.setString(3, patient.getMiddleName());
			this.preparedStatement.setString(4, patient.getPhoneNumber());
			
			this.preparedStatement.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	/*
	 * Функция обновления пациента,
	 * возвращает true в случае успеха,
	 * иначе false
	 */
	@Override
	public boolean update(Patient patient) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("UPDATE patients "
							+ "SET name = ?, surname = ?, middle_name = ?, phone_number = ? "
							+ "WHERE id_patient = ?");
			this.preparedStatement.setLong(5, patient.getIdPatient());
			this.preparedStatement.setString(1, patient.getName());
			this.preparedStatement.setString(2, patient.getSurname());
			this.preparedStatement.setString(3, patient.getMiddleName());
			this.preparedStatement.setString(4, patient.getPhoneNumber());
			this.preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция получения пациента,
	 * в качестве парметра функция получает id пациента
	 * возвращает экземпляр объекта класса Patient
	 */
	public Patient getPatient(long id) {
		Patient patient = new Patient();
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("SELECT * FROM patients WHERE id_patient = ?");
			this.preparedStatement.setLong(1, id);
			ResultSet res = this.preparedStatement.executeQuery();
			res.next();
			patient.setIdPatient(res.getLong("id_patient"));
			patient.setName(res.getString("name"));
			patient.setSurname(res.getString("surname"));
			patient.setMiddleName(res.getString("middle_name"));
			patient.setPhoneNumber(res.getString("phone_number"));
			
			return patient;
		} catch (SQLException e) {
			//e.printStackTrace();
			return patient = null;
		}
	}
}
