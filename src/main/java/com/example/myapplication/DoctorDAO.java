package com.example.myapplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Класс для работы с данными из таблицы Doctors
public class DoctorDAO extends DAO<Doctor> {
	
	public DoctorDAO() {
		connetctionToDB();
	}
	
	@Override
	public List<Doctor> getAll() throws SQLException {
		this.statement = getConnection().createStatement();
		String sql = "SELECT * FROM doctors";
		ResultSet docotrsResultSet = getStatement().executeQuery(sql);
		List<Doctor> listDoctor = new ArrayList<Doctor>();
		while (docotrsResultSet.next()) {
			listDoctor.add(new Doctor(
					 docotrsResultSet.getLong("id_doctor")
					,docotrsResultSet.getString("name")
					,docotrsResultSet.getString("surname")
					,docotrsResultSet.getString("middle_name")
					,docotrsResultSet.getString("specialty")
			));
		}
		
		return listDoctor;
	}
	
	/*
	 * Функция удаления доктора по его id,
	 * в качестве параметра в функцияю передаётся id доктора,
	 * которого требуется удалить,
	 * возвращает true в слкчае успеха,
	 * иначе false
	 */
	@Override
	public boolean delete(long id) {
		try {
			this.preparedStatement = getConnection().prepareStatement("DELETE FROM doctors WHERE id_doctor = ?");
			getPreparedStatement().setLong(1, id);
			getPreparedStatement().execute();
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция добавления нового доктора
	 * получает в качестве параметра экземпляр класса Doctor
	 * возвращает true если добавление произошло успешно,
	 * иначае false
	 */
	@Override
	public boolean insert(Doctor doctor) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("INSERT INTO doctors(name, surname, middle_name, specialty) "
							+ "VALUES (?, ?, ?, ?)");
			
			this.preparedStatement.setString(1, doctor.getName());
			this.preparedStatement.setString(2, doctor.getSurname());
			this.preparedStatement.setString(3, doctor.getMiddleName());
			this.preparedStatement.setString(4, doctor.getSpecialty());
			
			this.preparedStatement.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	/*
	 * Функция обновления доктора,
	 * возвращает true в случае успеха,
	 * иначе false
	 */
	@Override
	public boolean update(Doctor doctor) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("UPDATE doctors "
							+ "SET name = ?, surname = ?, middle_name = ?, specialty = ? "
							+ "WHERE id_doctor = ?");
			this.preparedStatement.setLong(5, doctor.getIdDoctor());
			this.preparedStatement.setString(1, doctor.getName());
			this.preparedStatement.setString(2, doctor.getSurname());
			this.preparedStatement.setString(3, doctor.getMiddleName());
			this.preparedStatement.setString(4, doctor.getSpecialty());
			this.preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция получения доктора,
	 * в качестве парметра функция получает id доктора
	 * возвращает экземпляр объекта класса Doctor
	 */
	public Doctor getDoctor(long id) {
		Doctor doctor = new Doctor();
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("SELECT * FROM doctors WHERE id_doctor = ?");
			this.preparedStatement.setLong(1, id);
			ResultSet res = this.preparedStatement.executeQuery();
			res.next();
			doctor.setIdDoctor(res.getLong("id_doctor"));
			doctor.setName(res.getString("name"));
			doctor.setSurname(res.getString("surname"));
			doctor.setMiddleName(res.getString("middle_name"));
			doctor.setSpecialty(res.getString("specialty"));
			
			return doctor;
		} catch (SQLException e) {
			return doctor = null;
		}
	}
	
	public List<StatisticsDoctor> getDoctorStatistics() {
		try {
			this.statement = getConnection().createStatement();
			/*
			 * Запрос возвращает ФИО доктора и первый подзапрос возвращает общее кол-во рецептов,
			 * что будет соответсвовать общему кол-ву рецептов у данного доктора.
			 * Так же второй подзапрос возвращает кол-во пациентов,
			 * которые закончили лечение у данного доктора,
			 * т.е. в соответсвии с датой окончания рецепта (term - дата окончания рецепта)
			 * можно судить о пациентах, которое прошли лечение. Т.е. term должна быть меньше,
			 * чем текущая дата.
			 * Третий подзапрос вернёт кол-во пациентов,
			 * которые ещё проходят лечение у данного доктора,
			 * т.е. term - должен быть больше текущей даты.
			 */
			String sql = "SELECT doctors.name AS name, doctors.surname AS surname, doctors.middle_name AS middle_name,"
					+ " (SELECT COUNT(*) FROM recipe WHERE recipe.id_doctor = doctors.id_doctor) AS countPatient,"
					+ " (SELECT COUNT(*) FROM recipe WHERE recipe.term < CURRENT_DATE AND recipe.id_doctor = doctors.id_doctor) AS countPatientEndTreated,"
					+ " (SELECT COUNT(*) FROM recipe WHERE recipe.term > CURRENT_DATE AND recipe.id_doctor = doctors.id_doctor) AS countPatientTreated"
					+ " FROM doctors";
			ResultSet res = getStatement().executeQuery(sql);
			List<StatisticsDoctor> listStatisticsDoctors = new ArrayList<StatisticsDoctor>();
			while (res.next()) {
				listStatisticsDoctors.add(new StatisticsDoctor(
						res.getString("name"), 
						res.getString("surname"), 
						res.getString("middle_name"), 
						res.getInt("countPatient"), 
						res.getInt("countPatientTreated"), 
						res.getInt("countPatientEndTreated")));
			}
			
			return listStatisticsDoctors;
		} catch (SQLException e) {
			return null;
		}
	}
	
}
