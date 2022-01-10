package com.example.myapplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Класс для работы с данными из таблицы Recipe
public class RecipeDAO extends DAO<Recipe> {
	
	public RecipeDAO() {
		connetctionToDB();
	}
	
	@Override
	public List<Recipe> getAll() throws SQLException {
		this.statement = getConnection().createStatement();
		String sql = "SELECT * FROM recipe";
		ResultSet recipesResultSet = getStatement().executeQuery(sql);
		List<Recipe> listRecipes = new ArrayList<Recipe>();
		while (recipesResultSet.next()) {
			listRecipes.add(new Recipe(
					 recipesResultSet.getLong("id_recipe")
					,recipesResultSet.getString("descriptions")
					,recipesResultSet.getDate("date_create")
					,recipesResultSet.getDate("term")
					,recipesResultSet.getLong("id_doctor")
					,recipesResultSet.getLong("id_patient")
					,recipesResultSet.getLong("id_priority")
			));
		}
		
		return listRecipes;
	}
	
	/*
	 * Функция удаления рецепта по его id,
	 * в качестве параметра в функцияю передаётся id рецепта,
	 * которого требуется удалить,
	 * возвращает true в случае успеха,
	 * иначе false
	 */
	@Override
	public boolean delete(long id) {
		try {
			this.preparedStatement = getConnection().prepareStatement("DELETE FROM recipe WHERE id_recipe = ?");
			getPreparedStatement().setLong(1, id);
			getPreparedStatement().execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция добавления нового рецепта
	 * получает в качестве параметра экземпляр класса Recipe
	 * возвращает true если добавление произошло успешно,
	 * иначае false
	 */
	@Override
	public boolean insert(Recipe recipe) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("INSERT INTO recipe(descriptions, date_create, term, id_doctor, id_patient, id_priority) "
							+ "VALUES (?, ?, ?, ?, ?, ?)");
			
			this.preparedStatement.setString(1, recipe.getDescriptions());
			this.preparedStatement.setDate(2, recipe.getDateCreate());
			this.preparedStatement.setDate(3, recipe.getTerm());
			this.preparedStatement.setLong(4, recipe.getIdDoctor());
			this.preparedStatement.setLong(5, recipe.getIdPatient());
			this.preparedStatement.setLong(6, recipe.getIdPriority());
			
			this.preparedStatement.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	/*
	 * Функция обновления рецепта,
	 * возвращает true в случае успеха,
	 * иначе false
	 */
	@Override
	public boolean update(Recipe recipe) {
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("UPDATE recipe "
							+ "SET descriptions = ?, date_create = ?, term = ?, id_doctor = ?, id_patient = ?, id_priority = ? "
							+ "WHERE id_recipe = ?");
			this.preparedStatement.setLong(7, recipe.getIdRecipe());
			this.preparedStatement.setString(1, recipe.getDescriptions());
			this.preparedStatement.setDate(2, recipe.getDateCreate());
			this.preparedStatement.setDate(3, recipe.getTerm());
			this.preparedStatement.setLong(4, recipe.getIdDoctor());
			this.preparedStatement.setLong(5, recipe.getIdPatient());
			this.preparedStatement.setLong(6, recipe.getIdPriority());
			this.preparedStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Функция получения рецепта,
	 * в качестве парметра функция получает id рецепта
	 * возвращает экземпляр объекта класса Pecipe
	 */
	public Recipe getRecipe(long id) {
		Recipe recipe = new Recipe();
		try {
			this.preparedStatement = getConnection()
					.prepareStatement("SELECT * FROM recipe WHERE id_recipe = ?");
			this.preparedStatement.setLong(1, id);
			ResultSet res = this.preparedStatement.executeQuery();
			recipe.setIdRecipe(res.getLong("id_recipe"));
			recipe.setDescriptions(res.getString("descriptions"));
			recipe.setDateCreate(res.getDate("date_create"));
			recipe.setTerm(res.getDate("term"));
			recipe.setIdDoctor(res.getLong("id_doctor"));
			recipe.setIdPatient(res.getLong("id_patient"));
			recipe.setIdPriority(res.getLong("id_priority"));
			
			return recipe;
		} catch (SQLException e) {
			e.printStackTrace();
			return recipe = null;
		}
	}
}
