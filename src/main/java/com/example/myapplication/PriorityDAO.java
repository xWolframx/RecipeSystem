package com.example.myapplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Класс для работы с данными из таблицы Priority
public class PriorityDAO extends DAO<Priority> {
	
	public PriorityDAO() {
		connetctionToDB();
	}
	
	@Override
	public List<Priority> getAll() throws SQLException {
		this.statement = getConnection().createStatement();
		String sql = "SELECT * FROM priority";
		ResultSet priorityResultSet = getStatement().executeQuery(sql);
		List<Priority> listPriority = new ArrayList<Priority>();
		while (priorityResultSet.next()) {
			listPriority.add(new Priority(
					 priorityResultSet.getLong("id_priority")
					,priorityResultSet.getString("name")
			));
		}
		
		return listPriority;
	}
	
	@Override
	public boolean insert(Priority priority) {
		return false;
	}
	
	@Override
	public boolean delete(long id) {
		return false;
	}
	
	@Override
	public boolean update(Priority priority) {
		return false;
	}
	
	/*
	 * Функция получения приоритета,
	 * в качестве парметра функция получает id приоритета
	 * возвращает экземпляр объекта класса Priority
	 */
	public Priority getPriority(long id) {
		Priority priority = new Priority();
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("SELECT * FROM priority WHERE id_priority = ?");
			this.preparedStatement.setLong(1, id);
			ResultSet res = this.preparedStatement.executeQuery();
			res.next();
			priority.setIdPriority(res.getLong("id_priority"));
			priority.setName(res.getString("name"));
			
			return priority;
		} catch (SQLException e) {
			return priority = null;
		}
	}
}
