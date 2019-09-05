package com.mrtoast.interviewProblems.fileNaming;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates backup files, renaming older backups if necessary
 */
public class FileNamer
{
	private final int maxBackups;
	
	public FileNamer(int maxBackups)
	{
		this.maxBackups = maxBackups;
	}
	
	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void backupOrCreateFile(Path path) throws IOException
	{
		synchronized (path)
		{
			if (path.toFile().exists())
			{
				backupExistingFile(path);
			}
			else
			{
				Files.createFile(path);
			}
		}
	}
	
	/**
	 * Generates backup files, renaming older backups if necessary..
	 * 
	 * @param directory Path
	 * @param filename String
	 * @throws IOException exception
	 */
	private void backupExistingFile(Path path) throws IOException
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
	 * Creates a new File in the provided directory with the backup filename.
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
	 * Generates the backup filename.
	 * 
	 * @param baseFileName the un-numbered filename
	 * @param number the backup number to append
	 * @return String the generated backup filename.
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
}
