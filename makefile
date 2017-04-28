sourcefiles = \
P2S.java \
P2SRequest.java \
P2SResponse.java \
P2PRequest.java \
P2PResponse.java \
Peer.java \
RFC.java \
Server.java \
Client.java \
MainActivity.java \

classfiles = $(sourcefiles:.java=.class)
#classfiles = P2S.class P2SRequest.class P2SResponse.class
all: $(classfiles)
%.class:%.java
	javac -d . -classpath . $<
clean:
	 rm -f *.class
jar: $(classfiles)
	jar cvmf manifest.txt server-client.jar com/csc573/p2p
