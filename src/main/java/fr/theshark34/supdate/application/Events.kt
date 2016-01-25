package fr.theshark34.supdate.application

import fr.theshark34.supdate.SUpdate
import fr.theshark34.supdate.files.FileAction
import java.io.File
import java.net.URL

/**
 * The Application Event
 *
 * This is an event for the application, given to give
 * some informations to the Application when an event
 * is called.
 *
 * This class is the parent class for all the event
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
open class ApplicationEvent(val sUpdate: SUpdate)

/**
 * The FileChecking Event
 *
 * This event is given in the onFileCheck application event
 * to give some informations about the checking file, and
 * things like this.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileCheckingEvent(sUpdate: SUpdate, val checkedFilePath: String, val checkResult: Boolean) : ApplicationEvent(sUpdate)

/**
 * The FileAction Event
 *
 * This event is given in the onFileAction applications
 * event, to give some informations about the current
 * action like which action it is, to which file, etc...
 *
 * This class is mostly a parent class for each Action event
 *
 * @version 3.2.0-BETA
 * @author TheShark34
 */
open class FileActionEvent(sUpdate: SUpdate, val action: FileAction, val targetFile: File) : ApplicationEvent(sUpdate)

/**
 * The 'Download' FileAction Event
 *
 * This event is called when the Download FileAction is
 * executed. It is given in argument to the onFileAction
 * method in the applications.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileActionDownloadEvent(sUpdate: SUpdate, file: File, val fileUrl: URL) : FileActionEvent(sUpdate, FileAction.DOWNLOAD, file)

/**
 * The 'Download' FileAction Event
 *
 * This event is called when the Download FileAction is
 * executed. It is given in argument to the onFileAction
 * method in the applications.
 *
 * @version 3.2.0-BETA
 * @author Litarvan
 */
class FileActionRenameEvent(sUpdate: SUpdate, file: File, val dest: File) : FileActionEvent(sUpdate, FileAction.RENAME, file)
