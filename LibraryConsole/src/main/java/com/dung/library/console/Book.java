package com.dung.library.console;

public class Book {
	private Long id;
	private String title;
	private String author;
	private boolean isBorrowed;
	private Long borrowerId;

	public Book() {
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isBorrowed() {
		return isBorrowed;
	}

	public void setBorrowed(boolean borrowed) {
		isBorrowed = borrowed;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	@Override
	public String toString() {
		return "Book{id=" + id + ", title='" + title + "', author='" + author + "', isBorrowed=" + isBorrowed
				+ ", borrowerId=" + borrowerId + "}";
	}
}