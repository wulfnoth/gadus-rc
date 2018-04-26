package org.wulfnoth.client;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;

import java.io.File;
import java.io.IOException;

public class SSHClient {

	private SshClient client;

	public SSHClient(String username, String host, int port, String privateKeyPath) throws IOException {
		client = SshClient.setUpDefaultClient();
		client.start();
		ConnectFuture connection = client.connect(username, host, port);
		connection.await();
		ClientSession session = connection.getSession();
		session.setKeyPairProvider(new FileKeyPairProvider(new File(privateKeyPath).toPath()));

		session.auth().verify();

		ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL);

		channel.setIn(new NoCloseInputStream(System.in));
		channel.setOut(new NoCloseOutputStream(System.out));
		channel.setErr(new NoCloseOutputStream(System.err));
//		channel.setErr(new NoCloseOutputStream(null));
		channel.open();
	}

	public static void main(String[] args) throws IOException {
		new SSHClient("cloud", "localhost", 3632, "/home/cloud/.ssh/id_rsa");
		new Thread(() -> {
			try {
				Thread.sleep(3600000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
