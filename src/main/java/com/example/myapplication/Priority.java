package com.example.myapplication;

/* Класс приоритета для хранения данных о приоритете
 * и дальнейшей обработки его данных
 */
public class Priority {
	private long id_priority;
	private String name;
	
	public Priority() {}
	
	public Priority(long id_priorty, String name) {
		this.id_priority = id_priorty;
		this.name = name;
	}
	
	public void setIdPriority(long id_priority) {
		this.id_priority = id_priority;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getIdPriority() {
		return this.id_priority;
	}
	
	public String getName() {
		return this.name;
	}
}
