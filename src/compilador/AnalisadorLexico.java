package compilador;

import java.util.ArrayList;
import java.util.Arrays;

public class AnalisadorLexico {
    
    private static AnalisadorLexico instance;
    
    //Array de numeros da gramática
    private final String[] n = {"0","1","2","3","4","5","6","7","8","9"};
    private final ArrayList<String> num = new ArrayList<String>(Arrays.asList(n));
    
    //Array de letras da gramática
    private final String[] l = {"a","b","c","d","e","f","g","h","i","j",
        "k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private final ArrayList<String> letras = new ArrayList<>(Arrays.asList(l));
    
    //Array de letras da gramática Maiuscula
    private final String[] m = {"A","B","C","D","E","F","G","H","I","J",
            "K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private final ArrayList<String> letrasm = new ArrayList<>(Arrays.asList(m));
    
    //Array de sinal da gramática
    private final String[] op = {",",";","(",")","{","}","[","]","+",
        "-","*","/","=","==", "<",">","!", "!=", "&", "&&", "|", "||", "'", "\"", "	", ".",":","?","\\"};
    private final ArrayList<String> sinais = new ArrayList<>(Arrays.asList(op));
    
    //Array de palavras reservadas da gramática
    private final String[] palavras = {"int","void","float","char","return",
        "if","while", "private", "boolean", "protected", "public", "final", "case", "else", "do",
        "for", "switch", "try", "catch", "throws", "byte", "char", "long", "double", "super", 
        "this", "static", "abstract", "class", "extends", "implements", "interface", "native", "new",
        "strictfp", "synchronized", "transient", "volatile", "break", "case", 
        "continue", "instanceof", "assert", "finally", "throw", "import", 
        "package", "short", "this", "const", "goto", "float", "boolean","true","false","null"}; 
    private final ArrayList<String> palavrasReservadas = 
            new ArrayList<>(Arrays.asList(palavras));
    
    //Array com os tokens encontrados durante a analise
    private ArrayList<String> tokens ;
    private String token;
    //booleano informando se houve erro na análise
    private boolean passou;
    private String[] simbolos;
    //armazena o caractere inválido, caso a analise encontre um erro
    private String simboloInvalido;
    
    
    private AnalisadorLexico(){
        this.tokens = new ArrayList<>();
        this.passou = false;
        this.token = "";
    }
    
    public static AnalisadorLexico getInstance(){
        if(instance == null){
            instance = new AnalisadorLexico();
        }
        return instance;
    }
    
    /*
    Adiciona o token encontrado ao Array de tokens, juntamente com o seu tipo
    */
    public void adicionarToken(String tipo){
        tokens.add(token+","+tipo);
        limparToken();
        passou = true;
    }
    
    public boolean analisar(String texto){
        texto = texto.replaceAll("\n", "");
        simbolos = texto.split("");
        
        for(int i=0; i < simbolos.length; i++){
            
            if(isLetra(simbolos[i])){
                token += simbolos[i];
                analisarPalavra(i);
            }
            
            else if(isSinal(simbolos[i])){
                /*
                & tem que ser seguida de outro &
                | tem que ser seguido de outro |
                caso contrário, é um simbolo inválido
                */
                if(simbolos[i].equals("&") || simbolos[i].equals("|")){
                    if(isOperadorDuplo(i)){
                        token += simbolos[i] + simbolos[i+1];
                        adicionarToken("OPERADOR");
                        i++;
                    }
                    else{
                        simboloInvalido = simbolos[i];
                        return false;
                    }
                }
                
                /*
                Se for um simbolo válido e for o ultimo caractere 
                a ser analisado então ele é adicionado a lista de tokens
                */
                else if((i == simbolos.length-1)){
                    token += simbolos[i];
                    adicionarToken("OPERADOR");
                }
                
                /*
                <,> e = podem ser seguidos de = para formar um simbolo duplo
                */
                else if((simbolos[i].equals("<") || simbolos[i].equals(">") ||
                        simbolos[i].equals("=")) && simbolos[i+1].equals("=")){
                    token += simbolos[i] + simbolos[i+1];
                    adicionarToken("OPERADOR");
                    i++;
                }
                
                else{
                    token += simbolos[i];
                    adicionarToken("OPERADOR");
                }
            }//fim do else if dos sinais
            
            else if(isNumero(simbolos[i])){
                analisarNumeros(i);
            }//fim do else if de numeros
            
            else{
                if(isEspaco(simbolos[i])){
                    limparToken();
                    passou = true;
                }
                else{
                    limparToken();
                    simboloInvalido = simbolos[i];
                    return false;
                }
            }
            
        }//fim do for
        
        return passou;
    }
    
    public boolean isOperadorDuplo(int index){
        
        if((index == simbolos.length-1)){
            token += simbolos[index];
            return false;
        }
        
        else if(((simbolos[index].equals("&")) && (simbolos[index+1].equals("&")))
                || (simbolos[index].equals("|") && (simbolos[index+1]).equals("|"))){
            return true;
        }
        
        else return (simbolos[index].equals("<") || simbolos[index].equals(">") || 
                simbolos[index].equals("=")) && (simbolos[index+1].equals("="));
    }
    
    public void analisarNumeros(int index){
        token += simbolos[index];
        
        if(!(token.isEmpty()) && isLetra(String.valueOf(token.charAt(0)))){
            analisarPalavra(index);
        }
        
        else if(index == simbolos.length-1){
            adicionarToken("NUM");
        }
        
        else if((isEspaco(simbolos[index+1]))
                || (isSinal(simbolos[index+1])) || isLetra(simbolos[index+1])){
            adicionarToken("NUM");
            
        }
        
    }
    
    public String getSimboloInvalido(){
        return simboloInvalido;
    }
    
    public ArrayList<String> getTokens(){
        return this.tokens;
    }
    
    public void analisarPalavra(int index){
        if((isLetra(simbolos[index])) || (isNumero(simbolos[index]))){
            
            //System.out.println("token - "+token);
            
            if(isPalavraReservada(token)){
                
                if(index == simbolos.length-1){
                    adicionarToken("PALAVRARESERVADA");
                }
                
                else if((isEspaco(simbolos[index+1])) 
                        || (isSinal(simbolos[index+1]))){
                    adicionarToken("PALAVRARESERVADA");
                }
            }
            
            else if(index == simbolos.length-1){
                adicionarToken("ID");
            }
            
            else if((isEspaco(simbolos[index+1])) || (isSinal(simbolos[index+1]))){
                adicionarToken("ID");
            }
        }

    }
    
    public boolean isEspaco(String valor){
        return (valor.equals(" ")) || (valor.equals(""));
    }
    
    public void limparToken(){
        this.token = "";
    }
  
    public void limparListaTokens(){
        tokens.clear();
    }
    public boolean isPalavraReservada(String valor){
        return (palavrasReservadas.contains(valor));
    }
    
    public boolean isSinal(String valor){
        return (sinais.contains(valor));
    }
        
    public boolean isLetra(String valor){
        return (letras.contains(valor)) || (letrasm.contains(valor));
    }
    
    public boolean isNumero(String valor){
        return (num.contains(valor));
    }
}