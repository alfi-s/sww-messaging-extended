# SWW Assessed Assignment 1 - Extended Messaging System
The aim of the assessment was to develop a more realistic messaging system for the command line interface. I used the sample solution provided from the quit exercise as a base and modified it heavily to implement the required features. The solution can be accessed in [this repository](https://git.cs.bham.ac.uk/ass782/sww-messaging-extended).

## 1. Register
When the client connects to a server, the client starts a thread called ClientLoginSequence which communicates with a thread called ServerLoginChecker which was started by the server. These threads allow the user to register and login without being allowed to send any other messages. They hold the input and output streams held by the Client and the Server when they are first run. The Client and the Server were modified to wait for these threads to stop rather than ClientSender/ClientReceiver/ServerSender/ServerReceiver as previously done.

It first prompts the user to type a command. When `register` is typed into the command, it then prompts the user to type a username and a password(see section 7.1). Once that has been entered, ClientLoginSequence sends those as strings to the ServerLoginChecker and waits for an answer. If the ServerLoginChecker finds that the name entered already belongs to a registered user, it sends an error string back to the ClientLoginSequence and then the threads wait for another command from the user. Otherwise, the ServerLoginChecker will send a success string and ClientSender/ClientReceiver/ServerSender/ServerReceiver Threads are created and started. This means that once a user registers, they are automatically logged in for convenience. The ServerLoginChecker also adds the user information to the ClientTable, which now holds a ConcurrentHashMap of "Account" objects. Account objects hold the information of each user when they register. They hold the username, a MessageLog object(see section 3), a hashmap of blocking queues(see section 2), and a password object (see section 7.1).

The ClientLoginSequence and the ServerLoginChecker then wait for these threads to be terminated by the `logout` function.

## 2. Login
This works similarly to the Register function, as it is handled in the ClientLoginSequence and the ServerLoginChecker. The difference is that the ServerLoginChecker checks if the name provided by the user is contained as a key within the ClientTable, and checks whether the password is correct (see section 7.1). The ServerLoginChecker then adds another blocking queue to the account. This is so that the system can accommodate multiple simultaneous logins. If the system only had one blocking queue per user (as previously done), then messages won't be sent all the client's instances as messages are placed only once. If you tried to get around this by adding messages multiple times to the blocking queue based on the number of simultaneous logins, that would not be a sufficient solution as it is hard to control which instance of the client the message goes to. That is why I used multiple blocking queues for each instance of a simultaneous login. The account can keep track of which blocking queue belongs to which instance by using an ID generated using `java.util.UUID` which is set when the client registers or logs in.

## 3. Logout
The logout function is similar to the `quit` function implemented in the previous exercise, but because this is merely removing the ClientSender/ClientReceiver/ServerSender/ServerReceiver threads and going back to the ClientLoginSequence/ServerLoginChecker, it counts as a logout rather than a quit function. When a logged-in user types `logout`, the ClientSender tells the ServerReceiver, then breaks out of its own while-loop to terminate. The ServerReceiver removes the BlockingQueue of this login instance as it is no longer needed. Then it breaks its while-loop and calls `interrupt` on ServerSender. It can do this because it has a reference `companion` passed in its constructor. The ServerSender then catches an InterruptedException, where it sends the string "quit" to the ClientReceiver and then ends. When the client receiver gets this string, it breaks out of it's own while-loop. Now that all four threads are terminated, control is given back to the ClientLoginSequence/ServerLoginChecker threads, where the user could register/login again or `quit` to close terminate the client itself.

### 3.1 Quit (Client)
The quit function is separate from the login function to allow the user to login or register as another user once they logout. This is implemented simply as the ClientLoginSequence tells the ServerLoginChecker that the user has typed "quit" and then the ClientLoginSequence terminates. The ServerLoginChecker also terminates upon receiving the message. Because the Client was waiting for the ClientLoginSequence to terminate, it now can close its streams and the socket and finally terminate.

## 4. Storing Messages
The ClientTable now holds a HashMap of Account objects instead of Blocking Queues. The Account object holds user information upon registration, and one of the things it holds is a MessageLog object. A MessageLog object has an ArrayList of an arbitrary type (in the case that a future implementation changes the Message type), and also a ListIterator of that ArrayList. It also keeps track of the current message. The MessageLog provides methods for traversing the ArrayList whilst updating the current message (see section 6).

The MessageLog of each user is updated independently of the Blocking Queues. This way, the MessageLog can keep track of all the messages so far, while the Blocking Queues exist to allow the ServerSender to send messages to the client regardless of simultaneous login.

## 5. New Send Syntax
The new send syntax is fairly straightforward to implement, namely it should simply take the command first before taking the name and text as was implemented in previous exercises. This was handled by the ClientSender and the ServerReceiever. Within their respective while-loops, they first handled the command by either taking the user input (client-side) or reading from the stream (server-side). Then, the command was matched with a switch case that allowed a number of commands to be implemented. During the "send" case, then the name of the recipient and the message was handled.

In the ServerReceiver, it is first checked whether or not the recipient exists in the ClientTable, otherwise an error will be sent. Next, it checks if the recipient is logged in by checking whether any Blocking Queues are present in the account of the recipient - if there are none, that means the recipient is not logged in. If the recipient is logged in, then all blocking queues will be updated with the message along with the recipient's MessageLog. If not, then only the MessageLog will be updated. This ensures that users can still send messages to logged-out users, which they will be able to access once they log back in.

## 6. Stored-Message Traversal / Removal
A user may see the messages that have been sent to them by typing `previous` or `next`. They may also delete any of these messages by typing `delete`.

This is primarily handled within the MessageLog class, which keeps track of the current message, and has a ListIterator that allows it to traverse the list. When a message is added, the iterator travels to the end of the list, adds the new message, and sets that as the current message.

When `previous` is called, the iterator calls its `previous()` method. This returns the element at the previous index and moves the iterator backwards. There may be the issue that the iterator could have been in front of the current message, and calling `previous()` would return the current message. To get around this, the iterator checks if the message is equal to the current message being tracked by the MessageLog object, and if so it calls `previous()` one more time, then sets it to the current message. `next` is implemented exactly the same, only using the iterator's `next()` command and checking forwards instead.

When `delete` is called, the iterator deletes the current message, then sets the current message to be the next message (if it exists). If it doesn't exist, it sets the current message to the previous message, and if it doesn't exist, then current message is set to null. There is no concrete reason that the next message is prioriteized over the previous message, this was chosen arbitrarily.

These methods may throw `NullPointerException` if the list is empty. These exceptions are caught in the ServerReceiver, where it then sends back an error message to the client.

## 7. Self-Chosen Feature(s)
I chose to implement a simple password encryption feature to validate users as they login, and also a procedure where the server could save data into a file which it could restore after the server has been terminated.

### 7.1 Password Encryption System
A new Password class was created to handle passwords and password encryption. It generally is a bad idea to store passwords in plain text, therefore the passwords are encrypted with the SHA-256 algorithm, which is an algorithm that can easily be done on a string, but difficult to reverse. The encryption was done simply by using the built-in `java.security.MessageDigest` class. The passwords are created, hashed, and then stored upon registration. When the user tries to login, the ServerLoginChecker hashes the password that the user inputted, and checks that against the hashed password stored in the user's account object. That way, there is no need to store the password in plain-text.

### 7.2 Server Data Storage
The server has the ability to save the ClientTable data to a file entitled `data.ctable` in the case that the server has been terminated. Initially, I inteded to save the data as objects using Serialization, but that would be an issue because in the case that I change any of the classes at a later date, the file couldn't be read. So instead, I decided to save a string which could be read and turned back into the ClientTable data.

Each account has a method `saveState()` that can generate a string of its information using a StringBuilder. Each account will be represented as a string in the following format:
```
\BEGIN ACCOUNT\
<NAME>
<PASSWORD_HASH>
From <recipient>: <text>
From <recipient>: <text>
From <recipient>: <text>
        .
        .
        .
From <recipient>: <text>
\END ACCOUNT\
```

The client table also has a method `saveTable()` which can then call the `saveState()` method for each account, then append them to a string. This will be used to save the data. The client table also has a method `loadTable()` to read a string with the above format and create data based on the string. This will be used to load the data.

The file I/O is handled by the FileKeeper class which has a method `writeData()` to save the clientTable data to a the `data.ctable` file, and also a method `readData()` to read the file and return a ClientTable. If the `readData()` method catches FileNotFoundException, then it will return an empty ClientTable.

These methods are then called within the Server class itself. When the server starts, it will call `readData()` to set the ClientTable to be used. A Shutdown hook was also implemented inside the Server class to call `writeData()` when the Server is closed. The server also has a thread called ServerSaver which saves calls this method every 5 minutes, in case the Server crashes. 
