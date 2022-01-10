package com.example.myapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

//Абстрактный класс для работы с данными БД
public abstract class DAO <T> {
	
	private Connection con = null;
	
	protected Statement statement = null;
	protected PreparedStatement preparedStatement = null;
	
	/*
	 * Функция загрузки драйвера БД
	 * Взвращает true, если драйвер загружен успешно, иначе false
	 */
	public static boolean loadDriverDB() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			return true;
		} catch (ClassNotFoundException exc) {
			return false;
		}
	}
	
	/*
	 * Функия подключении кБД
	 * Возвращает true если подключение удалось, иначе false
	 */
	public boolean connetctionToDB() {
		try {
			con = DriverManager.getConnection("jdbc:hsqldb:file:DateBase/testdb", "SA", "");
			return true;
		} catch (SQLException exc) {
			return false;
		}
	}
	
	/*
	 * Функция создания БД
	 * Получает в качестве параметра адрес sql - файла,
	 * sql - файл находится в src/main/resource/createDB.sql
	 * Возвращает true при успешном создании БД, иначе false
	 */
	public static boolean createDB(String pathSQLFile) throws IOException, ClassNotFoundException {
		File file = new File(pathSQLFile);
		String sqlCreateBD = "";
		//Получение sql - файла на чтение
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			sqlCreateBD += line; 
		}
    	bufferedReader.close();
    	
    	//Подключение к БД и создание БД
    	try {
    		Class.forName("org.hsqldb.jdbcDriver");
			Connection con = DriverManager.getConnection("jdbc:hsqldb:file:DateBase/testdb", "SA", "");
			Statement stat = con.createStatement();
			stat.execute(sqlCreateBD);
			stat.execute("INSERT INTO priority(name) VALUES('Нормальный')");
			stat.execute("INSERT INTO priority(name) VALUES('Срочный')");
			stat.execute("INSERT INTO priority(name) VALUES('Немедленный')");
			stat.execute("SHUTDOWN");
			return true;
		} catch (SQLException exc) {
			return false;
		}
	}
	
	//Функция закрытия БД
	public static void shutdown() throws SQLException {
		String sql = "SHUTDOWN";
		Connection con = DriverManager.getConnection("jdbc:hsqldb:file:DateBase/testdb", "SA", "");
		Statement stat = con.createStatement();
		stat.execute(sql);
	}
	
	//Функция установки соединения с БД
	public Connection getConnection() {
		return this.con;
	}
	
	/*
	 * Функция возвратат списка записей из определённой таблице
	 * возвращает List определённых экземпляров класса,
	 * которые соответсвуют таблице
	 */
	public abstract List<T> getAll() throws SQLException;
	
	/*
	 * Функция удаления определённной записи из таблицы,
	 * в каачестве параметра получает id удаляемой записи
	 * При успешном удалении возвращает true,
	 * иначе false
	 */
	public abstract boolean delete(long id);
	
	/*
	 * Функция втсавки новой записи в соответствущую таблицу,
	 * в качестве параметра принимает экземпляр класса соответсвующей таблицы,
	 * в случии успешной втавки возвращает true,
	 * иначе false
	 */
	public abstract boolean insert(T insertObject);
	
	/*
	 * Функция обновления записи в соответствущую таблице,
	 * в качестве параметра принимает экземпляр класса соответсвующей таблицы,
	 * в случии успешной втавки возвращает true,
	 * иначе false
	 */
	public abstract boolean update(T updateObject);
	
	//Функция получения объекта для выполнения запросов ез параметров
	public Statement getStatement() {
		return this.statement;
	}
	
	//Функция получения объекта для выполнения запросов с параметром
	public PreparedStatement getPreparedStatement() {
		return this.preparedStatement;
	}
	
	//Функция подтверждения транзакции
	public void commit() throws SQLException {
		this.statement = getConnection().createStatement();
		getStatement().execute("COMMIT");
	}

}
