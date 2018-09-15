/* Created by JReleaseInfo AntTask from Open Source Competence Group */
/* Creation date Fri Mar 09 13:17:21 PST 2018 */
package probe;

import java.util.Date;

/**
 * This class provides information gathered from the build environment.
 * 
 * @author JReleaseInfo AntTask
 */
public class MyReleaseInfo {


   /** buildDate (set during build process to 1520630241881L). */
   private static Date buildDate = new Date(1520630241881L);

   /**
    * Get buildDate (set during build process to Fri Mar 09 13:17:21 PST 2018).
    * @return Date buildDate
    */
   public static final Date getBuildDate() { return buildDate; }


   /** project (set during build process to "Load Cell Plot"). */
   private static String project = "Load Cell Plot";

   /**
    * Get project (set during build process to "Load Cell Plot").
    * @return String project
    */
   public static final String getProject() { return project; }


   /** buildTimeStamp (set during build process to "time to go"). */
   private static String buildTimeStamp = "time to go";

   /**
    * Get buildTimeStamp (set during build process to "time to go").
    * @return String buildTimeStamp
    */
   public static final String getBuildTimeStamp() { return buildTimeStamp; }


   /** copyright (set during build process to "2013, Eli Morris and The University of California, Santa Cruz"). */
   private static String copyright = "2013, Eli Morris and The University of California, Santa Cruz";

   /**
    * Get copyright (set during build process to "2013, Eli Morris and The University of California, Santa Cruz").
    * @return String copyright
    */
   public static final String getCopyright() { return copyright; }


   /** mail (set during build process to "ermorris@ucsc.edu"). */
   private static String mail = "ermorris@ucsc.edu";

   /**
    * Get mail (set during build process to "ermorris@ucsc.edu").
    * @return String mail
    */
   public static final String getMail() { return mail; }


   /** version (set during build process to "1.0"). */
   private static String version = "1.0";

   /**
    * Get version (set during build process to "1.0").
    * @return String version
    */
   public static final String getVersion() { return version; }


   /** company (set during build process to "University of California, Santa Cruz"). */
   private static String company = "University of California, Santa Cruz";

   /**
    * Get company (set during build process to "University of California, Santa Cruz").
    * @return String company
    */
   public static final String getCompany() { return company; }


   /**
    * Get buildNumber (set during build process to 489).
    * @return int buildNumber
    */
   public static final int getBuildNumber() { return 489; }


   /** home (set during build process to "http://es.ucsc.edu/~emorris"). */
   private static String home = "http://es.ucsc.edu/~emorris";

   /**
    * Get home (set during build process to "http://es.ucsc.edu/~emorris").
    * @return String home
    */
   public static final String getHome() { return home; }

}
