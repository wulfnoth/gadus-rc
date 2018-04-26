package org.wulfnoth.server;

import org.apache.sshd.common.util.OsUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SSHServer {

	private static final Logger log = LoggerFactory.getLogger(SSHServer.class);

	public static final SSHServer INS = new SSHServer();

	private final SshServer server;

	private SSHServer() {
		server = SshServer.setUpDefaultServer();
		server.setHost("localhost");
		server.setPort(3632);
		server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		server.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(new File("/home/cloud/.ssh/id_rsa.pub"), false));
		if (OsUtils.isUNIX()) {
			server.setShellFactory(
					new ProcessShellFactory("/bin/sh", "-i", "-l"));
		} else {
			System.exit(-1);
		}
	}

	public void start() throws IOException {
		log.debug("start");
		server.start();
	}

	public void stop() throws IOException {
		server.stop();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		INS.start();
		new Thread(() -> {
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
