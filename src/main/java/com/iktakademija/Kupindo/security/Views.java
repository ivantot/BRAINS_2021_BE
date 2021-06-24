package com.iktakademija.Kupindo.security;

public class Views {

	public static class Public {
	}

	public static class Private extends Public {
	}

	public static class CEO extends Private {

	}

	public static class Admin extends CEO {
	}
}
