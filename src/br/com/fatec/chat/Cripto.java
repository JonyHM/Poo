package br.com.fatec.chat;

public class Cripto {
	
	public static String encriptar(String mensagemCrua) {
		StringBuilder encriptada = new StringBuilder();
		char letraEncriptada = 'P';
				
		for(int i = 0; i < mensagemCrua.length(); i++) {
			int letra = (int) mensagemCrua.charAt(i);
			
			if(letra > 64 && letra < 91) {				
				if(letra <= 73) {
					letraEncriptada = (char) ( letra + 17 );
				} else {
					int resto = ( letra + 17 ) - 90;
					letraEncriptada = (char) ( 65 + resto );
				}

			} else if(letra > 96 && letra < 123) {
				if(letra <= 105) {
					letraEncriptada = (char) ( letra + 17 );
				} else {
					int resto = ( letra + 17 ) - 122;
					letraEncriptada = (char) ( 97 + resto );
				}
			} else {
				letraEncriptada = (char) letra;
			}
			
			encriptada.append(letraEncriptada);			
		}
		return encriptada.toString();
	}
	
	public static String decriptar(String mensagemEncriptada) {
		StringBuilder decriptada = new StringBuilder();
		char letraDecriptada = 'P';
		
		for(int i = 0; i < mensagemEncriptada.length(); i++) {
			int letra = (int) mensagemEncriptada.charAt(i);
			
			if(letra > 64 && letra < 91) {				
				if(letra >= 82) {
					letraDecriptada = (char) ( letra - 17 );
				} else {
					int resto = 65 - ( letra - 17 );
					letraDecriptada = (char) ( 90 - resto );
				}
				
			} else if(letra > 96 && letra < 123) {
				if(letra >= 114) {
					letraDecriptada = (char) ( letra - 17 );
				} else {
					int resto = 97 - ( letra - 17 );
					letraDecriptada = (char) ( 122 - resto );
				}
			} else {
				letraDecriptada = (char) letra;
			}
			
			decriptada.append(letraDecriptada);			
		}
		return decriptada.toString();
	}
}
