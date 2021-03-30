import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //REPLACE WITH YOUR CODE
        String slicedString = null;
        for (int i = whichSlice; i < message.length(); i = i + totalSlices){
            slicedString = slicedString + message.substring(i, i + 1);
        }
        return slicedString;
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        //WRITE YOUR CODE HERE
        CaesarCracker x = new CaesarCracker(mostCommon);
        for(int i = 0; i < klength; i++){
            String slicedEncrypted = sliceString(encrypted, i, klength);
            int subkey = x.getKey(slicedEncrypted);
            key[i] = subkey;

        }
        return key;
    }
    
    public HashSet readDictionary(FileResource fr){
        HashSet<String> dictionary = new HashSet<String>();
        for(String line : fr.lines()){
            line = line.toLowerCase();
            if(!dictionary.contains(line)){
            dictionary.add(line);
        }
        }
        return dictionary;
    }
    
    public int countWords(String message, HashSet<String> dictionary){
        int validwords = 0;
        for(String word: message.split("\\W")){
            word = word.toLowerCase();
            if(dictionary.contains(word)){
                validwords ++;
            }
        }
        return validwords;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary, char mostCommon){
        int countvalidmax = 0;
        String FinalResult = null;
        int KeyLength = 0;
        for(int k = 1; k < 101; k++){
            int[] key = tryKeyLength(encrypted, k, mostCommon);
            VigenereCipher VC = new VigenereCipher(key);
            String decryptResult = VC.decrypt(encrypted);
            int countvalid = countWords(decryptResult, dictionary);
            if(countvalid > countvalidmax){
                countvalidmax = countvalid;
                FinalResult = decryptResult;
                KeyLength = k;
            }
        }
        System.out.println("Key length is :"+ KeyLength);
        System.out.println("Number of valid word is :"+ countvalidmax);
        return FinalResult;
    }
    
    public char mostCommonCharIn(HashSet<String> dictionary){
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        char mostCommon = 'a';
        int count =0;
        for(String s: dictionary){
            for(int i = 0; i < s.length(); i++){
                char now = s.charAt(i);
                if(map.containsKey(now)){
                    map.put(now, map.get(now) + 1);
                }else{
                    map.put(now, 1);
                }
            }
        }    
        for(char c: map.keySet()){//maybe need Character here
                if(map.get(c) > count){
                    count = map.get(c);
                    mostCommon = c;
                }
            }
            return mostCommon;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>>languages){
        int validWord = 0;
        int validWordCurr = 0;
        String lanChosen = null;
        String Final = null;
        for(String lan: languages.keySet()){
            HashSet<String> dic = languages.get(lan);
            char mostCommon = mostCommonCharIn(dic);
            System.out.println("most common char for "+ lan + " is: " + mostCommon);
            String FinalforLan = breakForLanguage(encrypted, dic, mostCommon);
            validWordCurr = countWords(FinalforLan, dic);
            if(validWordCurr > validWord){
                validWord = validWordCurr;
                lanChosen = lan;
                Final = FinalforLan;
            }
        }
        System.out.println("The language is " + lanChosen+", the number of valid word is "+ validWord);
        System.out.println(Final);
    }
    
    public void breakVigenere () {
        //WRITE YOUR CODE HERE
        FileResource fr = new FileResource();
        String FileContent = fr.asString();
        
        DirectoryResource DicDr = new DirectoryResource();
        HashSet<String> dictionaryUse = new HashSet<String>();
        HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
        for (File f : DicDr.selectedFiles()){
            String name = f.getName();
            FileResource frLan = new FileResource(f);
            dictionaryUse = readDictionary(frLan);
            map.put(name, dictionaryUse);
            }
        breakForAllLangs(FileContent, map);
            
        //char mostcommon = 'e'; 
        //int[] key = tryKeyLength(FileContent, 4, mostcommon);
        //VigenereCipher VC = new VigenereCipher(key);
        //String decryptResult = VC.decrypt(FileContent);
        //String decryptResult = breakForLanguage(FileContent, dictionaryUse);
        //System.out.print(decryptResult);
        }
    
    }
    
