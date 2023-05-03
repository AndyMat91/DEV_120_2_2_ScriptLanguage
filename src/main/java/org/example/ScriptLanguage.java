package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class ScriptLanguage {
    private final Map<String, String > variables = new HashMap<>();

        public void read() {
            System.out.println("Введите имя файла, содержащего скрипты:");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            try (BufferedReader br = new BufferedReader(new FileReader(str))) {
                String s;
                while ((s = br.readLine()) != null) {
                    if (s.isEmpty()) continue;
                    StringBuilder word = new StringBuilder();
                    char[] charArray = s.toCharArray();
                    char c;
                    for (int i = 0; i < s.length(); i++) {
                        c = charArray[i];
                        if (charArray[0] == '#')
                            break;
                        if (String.valueOf(word).equals("print")) {
                            printString(s.substring(i + 1));
                            break;
                        }
                        if (String.valueOf(word).equals("set")) {
                            set(s.substring(i + 1));
                            break;
                        }
                        if (Character.isLetter(c)) {
                            word.append(c);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Введите верное имя файла!");
                read();
            }
        }


    private void set(String s) {
        StringBuilder value = new StringBuilder();
        boolean fv = false;
        char[] charArray = s.toCharArray();
        Character curChar;
        StringBuilder word = new StringBuilder();
        StringBuilder wordName = new StringBuilder();
        StringBuilder finalStr = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            curChar = charArray[i];
            if (fv) {

                if (curChar.equals('='))
                    continue;
                if (curChar.equals('$') || Character.isLetter(curChar) || Character.isDigit(curChar) || curChar.equals('_')) {
                    word.append(curChar);
                    if (!String.valueOf(word).contains("$"))
                        value = new StringBuilder(String.valueOf(word));
                    continue;
                }
                if (Character.isWhitespace(curChar)) {
                    if (String.valueOf(word).equals(""))
                        continue;
                    if (!String.valueOf(word).contains("$")) {
                        finalStr.append(word);
                        value = new StringBuilder(String.valueOf(word));
                        word.delete(0,word.length());
                        continue;
                    }
                    if (variables.containsKey(String.valueOf(word)))
                        finalStr.append(variables.get(String.valueOf(word)));
                    else {
                        System.out.println("Неизвестная переменная " + word + "!!!");
                    }
                    if (!String.valueOf(word).contains("$"))
                        value = new StringBuilder(String.valueOf(word));
                    word.delete(0, word.length());
                    continue;
                }
                if (curChar.equals('-') || curChar.equals('+'))
                    finalStr.append(curChar);
                if (Character.isDigit(curChar)) {
                    value.append(curChar);
                    continue;
                }
            }
            if (Character.isLetter(curChar) || curChar.equals('$') ||
            Character.isDigit(curChar) || curChar.equals('_')) {
                wordName.append(curChar);
            } else {
                fv = true;
            }
        }
        finalStr.append(value);
        value = new StringBuilder(String.valueOf(calculate(String.valueOf(finalStr))));
        if (!value.toString().equals(""))
            variables.put(String.valueOf(wordName), value.toString());
    }

    public static int calculate(String str) {
        StringBuilder operands = new StringBuilder();
        StringBuilder strForCalc = new StringBuilder();
        char curChar;
        Deque<Integer> stack = new ArrayDeque<>();
        StringTokenizer st;
        for (int i = 0; i < str.length(); i++) {
            curChar = str.charAt(i);
            if (curChar == '-' || curChar == '+') {
                while (operands.length() > 0) {
                    strForCalc.append(" ");
                    break;
                }
                strForCalc.append(" ");
                operands.append(curChar);
            } else {
                strForCalc.append(curChar);
            }
        }
        while (operands.length() > 0) {
            strForCalc.append(" ").append(operands.substring(operands.length() - 1));
            operands.setLength(operands.length() - 1);
        }
        int v1;
        int v2;
        String curStr;
        st = new StringTokenizer(strForCalc.toString());
        while (st.hasMoreTokens()) {
            curStr = st.nextToken().trim();
            if (curStr.length() == 1) {
                v2 = stack.pop();
                v1 = stack.pop();
                switch (curStr.charAt(0)) {
                    case '+':
                        v1 += v2;
                        break;
                    case '-':
                        v1 -= v2;
                        break;
                    default:
                }
                stack.push(v1);
            } else {
                try {
                    v1 = Integer.parseInt(curStr);
                    stack.push(v1);
                } catch (NumberFormatException e) {
                    System.out.println("Неверная строка для вычислений.");
                }
            }
        }
        return stack.pop();
    }

    private void printString(String s) {
        StringBuilder word = new StringBuilder();
        StringBuilder fin = new StringBuilder();
        char[] charArray = s.toCharArray();
        Character c;
        int fs = 0;
        for (int i = 0; i < s.length(); i++) {
            c = charArray[i];
            if (i == s.length() - 1) {
                if (!c.equals('"'))
                    word.append(c);
                if (variables.containsKey(String.valueOf(word)))
                    System.out.print(variables.get(String.valueOf(word)));
                else
                    System.out.print(word);
                break;
            }
            if (c.equals('\"')) {
                if (!Character.isWhitespace(c)) {
                    if (variables.containsKey(String.valueOf(word)))
                        System.out.print(variables.get(String.valueOf(word)));
                    else
                        System.out.print(word);
                    word.delete(0, word.length());
                }
                fs++;
                if (fs == 1)
                    continue;
            }
            if (fs == 1) {
                word.append(c);
                continue;
            }
            if (fs == 2) {
                fs = 0;
                fin.append(word);
                word.delete(0, word.length());
                continue;
            }
            if (Character.isLetter(c) || c.equals('$') || Character.isDigit(c) || c.equals('_')) {
                word.append(c);
            }
        }
        System.out.println(fin);
    }
}