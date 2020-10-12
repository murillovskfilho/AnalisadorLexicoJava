package fachada;

import compilador.AnalisadorLexico;
import java.util.ArrayList;
 public class Construtor {
    
    private final AnalisadorLexico alexico = AnalisadorLexico.getInstance();
    
    public boolean analisarLexicamente(String texto){
        return alexico.analisar(texto);
    }
    
    public ArrayList<String> getTokens(){
        return alexico.getTokens();
    }
    
    public void limparListaTokens(){
        alexico.limparListaTokens();
    }
    
    public String getSimboloInvalido(){
        return alexico.getSimboloInvalido();
    }
}
