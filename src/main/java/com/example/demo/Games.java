package com.example.demo;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="game")
public class Games {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="game_id")
	private Long id;
	
	@Column(name="Title")
	 private String title;
	
	
	@Column(length = 1000)
	
	    private String description;
	
	@Column(name="genre")
	    private String genre;
		@Column(name="image")
	    private String imageUrl;
		
		@Column(name="players")
	    private String players;
		
		public String getPlayers() {
			return players;
		}

		public void setPlayers(String players) {
			this.players = players;
		}

	

		@Column(name="time")
		private String time;
		
		
		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		
		@Override
		public String toString() {
			return "Games [id=" + id + ", title=" + title + ", description=" + description + ", genre=" + genre
					+ ", imageUrl=" + imageUrl + ", players=" + players + ", time=" + time + "]";
		}

		public Games(String title, String description, String genre, String imageUrl, String players,
				String time) {
	
			this.title = title;
			this.description = description;
			this.genre = genre;
			this.imageUrl = imageUrl;
			this.players = players;
			this.time = time;
		}

		public Games() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getGenre() {
			return genre;
		}

		public void setGenre(String genre) {
			this.genre = genre;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		
		
	

}
