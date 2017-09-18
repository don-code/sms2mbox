sms2mbox
========

This application converts SMS messages stored by the AOSP Messaging app (as a SQLite database) to Unix mailboxes, suitable for importing into another application. It was designed specifically to run detached from the phone (in other words, not as an app running on the phone) so that it can be used to work with messages from otherwise bricked phones.

## Usage
The tool requires that the SMS database has been pulled off a device already. Depending on the state of the device, this can be done via recovery with MTP, retrieval of the file from a whole-device image, or otherwise.

Run Maven to build the JAR:
```bash
mvn package
```

...then run the JAR against that DB, specifying the filenames for the inbox and sentbox files:

```bash
java -jar sms2mbox.jar sms.db inbox.mbox sent.mbox
```

The resulting mailboxes can be imported into a mail client such as Evolution or Thunderbird.

## Wish List
This application was originally purpose-written to pull old messages from a phone without a working screen, and then to search for a message containing a particular string. As such, it lacks many features which could conceivably be useful:
* **Batch processing.** The JavaMail API is geared towards batch updates of messages, whereas this tool writes them one by one to the mailbox. This incurs a significant performance penalty.
* **Matching of phone numbers to address book entries.** Currently, the `To:` and `From:` fields show the phone number.
* **MMS attachment support.** Only the text portion of MMS messages is imported at this time; any assets such as images are stripped.
* **Normalization of phone numbers.** Depending on your device, received and sent messages may differ in how they represent phone numbers (e.g. received messages may be of the format `12345678901` whereas sent messages may be of the format `+1 (234) 567-8901`. This prevents utilizing the threaded view offered by many mail clients
