package net.coldbird.bcfusefs;

import java.util.Scanner;

import net.coldbird.bcfusefs.driver.BitcasaFS;
import net.fusejna.FuseException;

import com.bitcasa.javalib.BitcasaClient;

public class CLIEntryPoint {

	// Second to Nanosecond Multiplier
	private static final long secondToNanosecondMultiplier = 1000000000;

	/**
	 * Show Mount Help Text
	 */
	private static void showHelp() {
		// Output Help Text
		System.err.println("Bitcasa Fuse Filesystem Driver by Coldbird <www.coldbird.net>");
		System.err.println("Usage: BitcasaFS <client-id> <client-secret> <cache-folder> <cache-size> <mountpoint> [request-timeout] [folder-cache-timeout] [verbose-mode]");
		System.err.println("<client-id>: Bitcasa Client ID (fetch this from the Bitcasa Website)");
		System.err.println("<client-secret>: Bitcasa Client Secret (fetch this from the Bitcasa Website)");
		System.err.println("<mountpoint>: Local Mount Path (folder has to exist already)");
		System.err.println("[request-timeout]: Bitcasa Request Repeat Timeout (in seconds, negative values or zero for infinite, default value is 90 seconds)");
		System.err.println("[folder-cache-timeout]: Bitcasa Folder Cache Timeout (in seconds, negative values or zero for infinite, you don't want that though, default value is 300 seconds)");
		System.err.println("[verbose-mode]: fuse-jna Debug Mode (true to enable verbose output, everything else validates to false and disables it, default value is false)");
	}
	
	/**
	 * CLI Entry Point
	 * @param args
	 */
	public static void main(String[] args) {
		// Named Arguments
		final String clientId = args[0];
		final String clientSecret = args[1];
		final String mountPoint = args[2];
		final String optRequestTimeout = args.length >= 4 ? args[3] : null;
		final String optFolderCacheTimeout = args.length >= 5 ? args[4] : null;
		final String optVerboseMode = args.length >= 6 ? args[5] : null;

		// Request Timeout
		long requestTimeout = 90 * secondToNanosecondMultiplier;

		// Folder Cache Timeout
		long folderCacheTimeout = 300 * secondToNanosecondMultiplier;
		
		// Verbose Mode Flag
		boolean verbose = false;

		// Invalid number of arguments detected
		if (args.length < 3 || args.length > 6) {
			// Output Help Text
			showHelp();
			
			// Exit Mount Application
			System.exit(1);
		}
		
		// Custom Request Timeout requested
		if (optRequestTimeout != null) {
			try {
				// Parse Request Timeout
				requestTimeout = Long.parseLong(optRequestTimeout);

				// Infinite Timeout requested
				if (requestTimeout <= 0) {
					// Set Infinite Timeout
					requestTimeout = Long.MAX_VALUE;
				}
				
				// Normal Timeout requested
				else {
					// Convert to Nanoseconds
					requestTimeout *= secondToNanosecondMultiplier;
				}
			} catch (final NumberFormatException e) {
				// Output Warning
				System.err.println("Invalid Request-Timeout given (" + optRequestTimeout + ") - falling back to 90 second timeout.");
			}
		}

		// Custom Folder Cache Timeout requested
		if (optFolderCacheTimeout != null) {
			try {
				// Parse Folder Cache Timeout
				folderCacheTimeout = Long.parseLong(optFolderCacheTimeout);

				// Infinite Timeout requested
				if (folderCacheTimeout <= 0) {
					// Set Infinite Timeout
					folderCacheTimeout = Long.MAX_VALUE;
				}
				
				// Normal Timeout requested
				else {
					// Convert to Nanoseconds
					folderCacheTimeout *= secondToNanosecondMultiplier;
				}
			} catch (final NumberFormatException e) {
				// Output Warning
				System.err.println("Invalid Folder-Cache-Timeout given (" + optFolderCacheTimeout + ") - falling back to 300 second timeout.");
			}
		}

		// Custom Verbosity Setting requested
		if (optVerboseMode != null) {
			// Save Verbosity Setting
			verbose = optVerboseMode.toLowerCase().trim().equals("true");
		}

		// Create Bitcasa client
		final BitcasaClient client = new BitcasaClient(clientId, clientSecret);

		// Bitcasa Authentication
		String input = null;

		// Line Reader for Input
		final Scanner scanner = new Scanner(System.in);

		// Not yet authenticated with Bitcasa
		while (client.getAccessToken() == null) {
			try {
				// Output Authorization Code URL
				System.out.println("Please go to " + client.getAuthenticateUrl() + " and get the authorization code or type \"exit\" to exit.");

				// Output Prompt
				System.out.print("> ");

				// Read Line from Input
				input = scanner.next();

				// Exit Request
				if (input.equals("exit")) {
					System.exit(0);
				}

				// Request Access Token from Bitcasa
				client.requestForAccessToken(input);
			} catch (final Exception e) {
				// Output Error Message
				System.err.println(e.getMessage());

				// Output Stack Call Trace
				System.err.println(e.getStackTrace());
			}
		}

		// Successfully authenticated with Bitcasa
		if (client.getAccessToken() != null) {
			try {
				// Mount Bitcasa Drive
				new BitcasaFS(client, requestTimeout, folderCacheTimeout, verbose).mount(mountPoint);
			} catch (FuseException e) {
				// Output Error Message
				System.err.println(e.getMessage());

				// Output Stack Call Trace
				System.err.println(e.getStackTrace());
			}
		}
	}

}
