package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CSVLoader {
	private static CSVLoader self;
	private static String recordsPath;
	private CSVLoader(){
	}
	public static CSVLoader call() {

		self = self == null ? new CSVLoader() :self;
		return self;
	}
	public static void createLogFile() {
		
	}
	public static <T> List<T> read(String path, Class<T> type){
		List<T> entries = new ArrayList<T>();
		List<Field> fields = new ArrayList<Field>(Arrays.stream(type.getDeclaredFields())
				.filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())))
				.collect(Collectors.toList()));
		Constructor<T> ctor = null;
		try{
			 ctor = type.getDeclaredConstructor();
		}catch(Exception e) {
			e.printStackTrace();
		}
		for( Field f : fields) {
			f.setAccessible(true);
		}
		try{
			for(String line : Files.readAllLines(Paths.get(path))) {
				T entry = ctor.newInstance();
				String[] fieldValues  = line.split(",");
				if(fieldValues.length == fields.size()) {
					int i = 0;
					for(Field f : fields) {
						fromStringToVal(entry,f,fieldValues[i++]);
						}
				}
				entries.add(entry);
			}
		}catch(Exception e) {
			System.out.print(e + ":" + e.getCause());
		} 
		return entries;
	}
	
	public static <T> T readSingleEntry(String path, Class<T> type){
		List<Field> fields = new ArrayList<Field>(Arrays.stream(type.getDeclaredFields())
				.filter(field -> !Modifier.isStatic(field.getModifiers()))
				.collect(Collectors.toList()));
		Constructor<T> ctor = null;
		try{
			 ctor = type.getDeclaredConstructor();
		}catch(Exception e) {
			System.out.print(e + ":" + e.getCause());
		}
		for( Field f : fields) {
			f.setAccessible(true);
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(path))){
				String[] lineValues = reader.readLine().split(",");
				T result = ctor.newInstance();
				if(lineValues.length == fields.size()) {
					int i = 0;
					for(Field f : fields) {
						fromStringToVal(result,f,lineValues[i]);
						i++;
					}
				}
				return result;
		}catch(Exception e) {
			System.out.print(e + ":" + e.getCause());
		} 
		return null;
	}
		
	//The CSV File must contain only values that the read() method can take and construct an object of type T with
	//for Food : name, carbs,proteins,fats,amount
	public static <T> void write(String filePath, List<T> entries) {
		List<Field >fields = Arrays.stream(entries.get(0).getClass().getDeclaredFields())
				.filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())))
				.collect(Collectors.toList());
		for(Field  f: fields) {
			f.setAccessible(true);
		}
	
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))){
			for(T entry : entries) {
				int i = 0;
				for(Field field : fields) {
					if( i == fields.size() - 1)
						break;
					try{
						Object fieldValue = field.get(entry);
						if(fieldValue != null ) {
							writer.write(fieldValue.toString());
							writer.write(',');
						}
					}catch(Exception e) {
						System.out.println( e + " " + e.getCause());
					}
					i++;
				}
				try{
					Object fieldValue = fields.get(fields.size() - 1).get(entry);
					if(fieldValue != null) {
						writer.append(fieldValue.toString());
						writer.newLine();
					}
				}catch(Exception e) {
					System.out.println( e + " " + e.getCause());
				}
			}
		
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static <T> void writeSingleEntry(String filePath,T entry) {
		List<Field >fields = Arrays.stream(entry.getClass().getDeclaredFields())
				.filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())))
				.collect(Collectors.toList());
		for(Field  f: fields) {
			f.setAccessible(true);
		}
		if(fields.size() == 0) {
			System.out.println("No fields to write for entry Type");
			return;
		}

		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))){
				int i = 0;
				for(Field field : fields) {
					if( i >= fields.size() - 1)
						break;
					try{
						Object fieldValue = field.get(entry);
						if(fieldValue != null ) {
							writer.write(fieldValue.toString());
							writer.write(',');
						}
					}catch(Exception e) {
						System.out.println( e + " " + e.getCause());
					}
					i++;
				}
				try{
					Object fieldValue = fields.get(fields.size() - 1).get(entry);
					if(fieldValue != null) {
						writer.write(fieldValue.toString());
						writer.newLine();
					}
				}catch(Exception e) {
					System.out.println( e + " " + e.getCause());
				}
		
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	private static <T> void  fromStringToVal(Object entry, Field field, String value) {
		//If the field is of type String,set it as a String
		Object setVal = null;
		if(field.getType().getSimpleName().equals("String")) {
			setVal = value;
		}
		//Otherwise, the value is a <? extends Number> so it must be parsed, if the number is not an int it is 
		else if (field.getType().isPrimitive()) {
			String numberTypeName = Character.toUpperCase(field.getType().getSimpleName().charAt(0)) + field.getType().getSimpleName().substring(1); 
			String completeName = numberTypeName;
			completeName +=  numberTypeName.equals("Int")  ? "eger" : "";
			try{
				Class<?> numberType = Class.forName("java.lang." + completeName);
				Method parsingMethod = numberType.getMethod("parse" + numberTypeName,String.class); 
				Object result = parsingMethod.invoke(null, value);
				setVal = result;
			}catch(Exception e) {
				System.out.println( e+  " " + e.getCause());
			}
		}
		else {
			//Otherwise the type of the field is a complex class which should have a constructor taking a String as only argument
			Class<?> customType = field.getType();
			try{
				Constructor<?> ctor = customType.getDeclaredConstructor((Class<?>)String.class);
				setVal = ctor.newInstance(value);
			}catch(Exception e) {
				System.out.println( e+  " " + e.getCause());
			}
		}
		try{
			field.set(entry, setVal);
		}catch(Exception e) {
			System.out.println( e+  " " + e.getCause());
		}
	}
	public static void recordAction(TimeStamp t) {
		//writeSingleEntry(logPath,t);
	}
	
}
