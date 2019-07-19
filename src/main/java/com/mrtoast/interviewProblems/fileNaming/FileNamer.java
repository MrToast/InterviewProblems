/* Copyright 2019 Jack Henry Software */

package com.mrtoast.interviewProblems.fileNaming;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileNamer
{
private final int maxBackups;
	
	private final Path outputFilePath;
	private final Formatter formatter;
	
	public FileNamer(Path outputFilePath, int maxBackups, Formatter formatter)
	{
		this.outputFilePath = outputFilePath;
		this.maxBackups = maxBackups;
		this.formatter = formatter;
	}
	
	public void writeHeader() throws IOException
	{
		backupOrCreateFile(outputFilePath);
		String header = formatter.writeHeader();
		write(header);
	}
	
	public void writeMessage() throws IOException
	{
		String message = formatter.writeMessage();
		write(message);
	}
	
	public void writeTail() throws IOException
	{
		String tail = formatter.writeTail();
		write(tail);
	}

	void write(String report) throws IOException
	{
		write(report, Charset.defaultCharset());
	}
	
	private void write(String report, Charset charset) throws IOException
	{
		writeBufferedWriter(outputFilePath, report, charset);
	}
	
	/**
	 * Appends the report to file.
	 * 
	 * All instances of ProfilerReporter synchronize on the path parameter of this
	 * method to prevent multiple instances from attempting to write to file simultaneously.
	 * 
	 * @param path Path
	 * @param report String
	 * @param charset Charset
	 * @throws IOException exception
	 */
	private static void writeBufferedWriter(Path path, String report, Charset charset) throws IOException
	{
		synchronized (path)
		{
			try(BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(path.toFile(), true), charset)))
			{
				writer.append(report);
			}
		}
	}
	
	
	
//	===============================
//	| Backup profiler report file |
//	===============================
	
	private void backupOrCreateFile(Path path) throws IOException
	{
		synchronized (path)
		{
			if (path.toFile().exists())
			{
				backupExistingReport(path);
			}
			else
			{
				Files.createFile(path);
			}
		}
	}
	
	/**
	 * Generates backup files of the profiler report file, renaming older backups if necessary.
	 * 
	 * All instances of ProfilerReporter synchronize on the path parameter of this
	 * method to prevent multiple instances from attempting to access a file simultaneously.
	 * 
	 * @param directory Path
	 * @param filename String
	 * @throws IOException exception
	 */
	private void backupExistingReport(Path path) throws IOException
	{
		synchronized (path)
		{
			// Use the canonical file to guarantee that there is a parent
			Path directory = path.toFile().getCanonicalFile().toPath().getParent();
			String filename = path.getFileName().toString();
			
			// If all backup slots are used, delete the oldest backup file.
			File oldestBackupFile = generateBackupFile(directory, filename, maxBackups);
			deleteBackupFile(oldestBackupFile);
			
			// Rename all backups sequentially to free the first backup slot.
			for(int i = maxBackups - 1; i > 0; i--)
			{
				File originalBackupFile = generateBackupFile(directory, filename, i);
				File renamedBackupFile = generateBackupFile(directory, filename, i+1);
				renameBackupFile(originalBackupFile, renamedBackupFile);
			}
			
			// Move the main file to the first backup slot.
			File backupFileOne = generateBackupFile(directory, filename, 1);
			renameBackupFile(path.toFile(), backupFileOne);
			
			// Create and return the new file with the original name.
			Files.createFile(path);
		}
	}
	
	/**
	 * Creates a new File in the provided directory with the profiler report backup filename.
	 * 
	 * @param directory Path
	 * @param filename String
	 * @param number int
	 * @return File file
	 */
	private static File generateBackupFile(Path directory, String filename, int number)
	{
		return new File(directory.toFile(), generateBackupFileName(filename, number));
	}
	
	/**
	 * Generates the profiler report backup filename.
	 * 
	 * @param baseFileName the un-numbered filename
	 * @param number the backup number to append
	 * @return String the generated profiler report backup filename.
	 */
	private static String generateBackupFileName(String baseFileName, int number) 
	{
		String backupName = baseFileName + "." + number;
		return backupName;
	}
	
	private static void deleteBackupFile(File backupFile) throws IOException
	{
		synchronized (backupFile)
		{
			if (backupFile.exists())
			{
				if (!backupFile.delete())
				{
					throw new IOException("File deletion failed: " + backupFile.toString());
				}
			}
		}
	}
	
	private static void renameBackupFile(File originalFile, File renamedFile) throws IOException
	{
		synchronized (originalFile)
		{
			if (originalFile.exists())
			{
				if (!originalFile.renameTo(renamedFile))
				{
						throw new IOException("File rename failed: " + originalFile.toString());
				}
			}
		}
	}
	
	static interface Formatter {
		
		public default String writeHeader() {
			return "Header" + System.lineSeparator();
		}
		
		public default String writeMessage() {
			return "Message" + System.lineSeparator();
		}

		public default String writeTail() {
			return "End" + System.lineSeparator();
		}
	}
}
