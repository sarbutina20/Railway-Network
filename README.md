This is a project created for the course Design Patterns and is guided by the requirements specified by the professor.
Here you can find the requirements for each phase of the assignment. Second phase expands the first and the third expands the second.
There are 3 phases and the last one contains the final project which contains a system for railway infrastructure management.

# Assignment 1. Railway Traffic

The company provides passenger and freight transport using railway infrastructure and railway transport vehicles. The railway infrastructure consists of a railway network made up of multiple railway lines (routes) that connect railway stations (terminals or stops).  

A railway line is a transportation route used by railway vehicles. It extends across a specific number of railway stations, with at least two: the starting station and the terminal station. A railway line should be viewed in both directions so that the terminal station in one direction becomes the starting station in the opposite direction, and vice versa.  

A railway line between two stations has specific characteristics: designation, category (local, regional, international), type of transport (classic—coal, diesel, batteries—or electric, which implies it can also support classic transport), number of tracks (single or double), length (0-999 km), permissible axle load (10-50 t/axle), permissible load per linear meter (2-10 t/m), and status (operational, defective, closed).  

If different characteristics exist along a railway line (e.g., axle load limit, number of tracks, etc.), a station may appear twice in sequence within the railway line. The first entry refers to the section between the preceding station and that station, while the second entry refers to the section between that station and the next one.  

For example, on railway line R201, the Varaždin station appears twice. The first entry pertains to the section between Čakovec and Varaždin (22.5 t/axle, 8.0 t/m), while the second refers to the section between Varaždin and Turčin (18.0 t/axle, 6.1 t/m). Another example is line M201, where the Križevci station appears twice. The first entry refers to the section between Majurec and Križevci (single track), while the second refers to the section between Križevci and Rapinec (double track).  

The railway network in Croatia is shown in the image [https://www.hzinfra.hr/wp-content/uploads/2024/01/IOM_2024_Hrvatska-verzija-karta-mreze.pdf](https://www.hzinfra.hr/wp-content/uploads/2024/01/IOM_2024_Hrvatska-verzija-karta-mreze.pdf). The data in the files contain part of the displayed railway network.  

The next elements of railway infrastructure are railway stations. A railway station is a point (in reality, a facility with associated infrastructure) on a railway line where railway vehicles can stop for passenger boarding and alighting.

Railway transport vehicles are used for transporting goods, enabling loading and unloading at railway stations. A single railway station can be located on one or more railway lines. This means that a railway vehicle can travel between stations that are part of one, two, or multiple railway lines.  

For example, the journey from the starting railway station **Donji Kraljevec** to the destination station **Zagreb Glavni Kolodvor** can be completed via two routes:  
- The first route follows railway lines **M501, R201, R202, M201, and M102**, passing through key stations: **Donji Kraljevec – Čakovec – Varaždin – Koprivnica – Dugo Selo – Zagreb Glavni Kolodvor**.  
- The second route follows railway lines **M501, R201, and M101**, passing through key stations: **Donji Kraljevec – Čakovec – Varaždin – Zaprešić – Zagreb Glavni Kolodvor**.  

### Railway Station Attributes:
- **Type**: terminal, stop  
- **Activities**: passenger boarding/alighting, freight loading/unloading, both  
- **Number of platforms**: 1-99  
- **Status**: open, closed  

### Railway Transport Vehicles:
Railway transport vehicles are vehicles that can operate exclusively on railway lines. A vehicle can function independently or as part of a **composition** of transport vehicles. A **composition** consists of at least two vehicles, where at least one must have a propulsion system to pull the other vehicles.  

### Transport Vehicle Attributes:
- **Purpose**:  
  - Self-propelled transport vehicle  
  - Self-propelled vehicle for pulling a composition  
  - Non-powered transport vehicle  
- **Type of transport**:  
  - None  
  - Passenger transport  
  - Sleeping cars  
  - Restaurant cars  
  - Freight transport for automobiles  
  - Freight transport for packaged goods in containers  
  - Freight transport for bulk cargo  
  - Freight transport for liquid cargo  
  - Freight transport for gaseous cargo  
- **Type of propulsion**: none, diesel, battery, electric  
- **Maximum power**: -1 (unknown), 0.0-10 MW  
- **Maximum speed**: 1-200 km/h  
- **Year of manufacture**  
- **Manufacturer**  
- **Capacity**: seats, standing places, beds, bicycles, cars  
- **Load capacity** (t)  
- **Volume** (m³)  
- **Status**: operational, defective  

If a **composition** is defined, it must include at least one **self-propelled vehicle for pulling** and at least one **non-powered vehicle (wagon)**. Exceptionally, a composition may consist solely of one or more self-propelled vehicles if they are being transferred to another railway station.  

### CSV File Format:
All files are encoded in **UTF-8** and use the **CSV (Comma Separated Values)** format, where values in each row are separated by **";"**.  
- The first row contains attribute names and is for informational purposes only—it should be skipped when loading data.  
- A completely empty row is not an error and should simply be skipped; it visually separates groups of rows for better readability.  
- A row starting with **"#"** is not an error; it serves as a comment related to the following row(s) and should be ignored when processing data.  
- **All attributes in the files are mandatory!**  
- The **Croatian date format (dd.mm.yyyy. hh:mm:ss)** must be used throughout the task.  

More details about the files and their attributes can be found in **Table 1**.

![image](https://github.com/user-attachments/assets/7325c740-d428-411e-a57d-310d3279dd62)

The provided files serve only as an example with specific content, but the instructor will conduct testing using different files and datasets. Some of these will be used during assignment presentations, while others will be reserved for testing and grading. Students are encouraged to prepare their own additional files to test their programs thoroughly. This can be achieved by adding new records to the provided files, modifying existing records, or deleting certain records. It is especially recommended to create test files that include both valid and invalid records to simulate various scenarios. For instance, some files may intentionally lack the first informational row, contain too few or too many attributes in a row, or include incorrect values in numeric fields, such as non-numeric characters in a field that requires numerical input. By preparing and testing with such variations, students can ensure their programs are robust and capable of handling different types of data effectively.

##  Problem Description

At the start, the company’s transport system for passengers and freight must be initialized by loading the necessary files in the correct order and creating the required objects for system execution. During each data file import, it is essential to verify the validity of each record. If a record is invalid, it should be skipped while logging the error number, the record's content, and an explanation of why it is incorrect. The error numbering includes both the total number of errors during system operation and the error's sequential number within the file. Once all necessary files have been loaded, the program should be prepared to execute commands in an interactive mode. The program will continue executing commands until the user enters the **Q** command.  

The **IP** command displays a table of railway lines, including their designation, starting and ending railway stations, and total kilometers.  

The **ISP oznakaPruge N** command displays a table of railway stations along the selected railway line in normal order. The table includes station names, types, and distances from the starting station. For example, on railway line **M501**, the stations are listed from **Kotoriba to Macinec**.  

The **ISP oznakaPruge O** command displays a table of railway stations along the selected railway line in reverse order. The table includes station names, types, and distances from the starting station. For example, on railway line **M501**, the stations are listed from **Macinec to Kotoriba**.

The **ISI2S polaznaStanica - odredišnaStanica** command displays a table of railway stations between two selected stations. The table includes station names, types, and distances from the starting station. If the two stations are on the same railway line, the table will list all the intermediate stations along that route. For example, executing **ISI2S Donji Kraljevec - Čakovec** will display stations along a single railway line.  

If the two stations are on different railway lines, the command will list all intermediate stations across multiple lines. For example, **ISI2S Donji Kraljevec - Zagreb glavni kolodvor** will display all stations from the departure station to the destination station, even if they are on different railway lines.  

The **IK oznaka** command displays a table of transport vehicles in a composition. The table includes the vehicle designation, role, description, year of production, purpose, type of propulsion, and maximum speed. For example, executing **IK 8001** will display all vehicles in composition **8001**.  

The **Q** command terminates the program. Once this command is entered, the program will stop execution and exit.  

The program must be implemented as a **command-line application** for an operating system terminal, meaning it **must not** have a graphical user interface (GUI) and **must not** be executed through an integrated development environment (IDE). When running the program, only the necessary data should be displayed, avoiding leftover debugging or test output.  

The program is executed by entering command-line arguments specifying the input data files. The execution follows this format:  
```bash
java -jar /home/UzDiz/DZ_1/sarbutina20_zadaca_1/target/sarbutina20_zadaca_1.jar --zs DZ_1_stanice.csv --zps DZ_1_vozila.csv --zk DZ_1_kompozicije.csv
```  
The order of options in the command line is arbitrary, so the program must correctly process them regardless of their sequence. The values for these options are also arbitrary and will be different during assignment presentations and testing. The filenames may also differ from those in the example.  

To run the program, the current working directory must first be set to the location where the data files are stored. For example:  
```bash
cd /home/UzDiz/DZ_1/podaci
```  
Since the program is executed from this location, **file paths should not be included in the filenames** when specifying input data files. Instead, the command should reference only the filename. However, the full path to the program’s executable file **must** be specified, as shown in this command:  
```bash
java -jar /home/UzDiz/DZ_1/sarbutina20_zadaca_1/target/sarbutina20_zadaca_1.jar --zs DZ_1_stanice.csv --zps DZ_1_vozila.csv --zk DZ_1_kompozicije.csv
```  

In the project's root directory, students must include a documentation file named **{LDAP_korisničko_ime}_zadaca_1.pdf**, following the guidelines provided in the documents *"Preporuke u vezi zadaća"* and *"Opći model ocjenjivanja zadaća"*.  

Only **design patterns** covered in lectures up to the assignment's release (the last being **Bridge**) are allowed. **Built-in language features** that implement design pattern functionalities must not be used. **External libraries or class packages are also prohibited**.  

Methods in classes **must not exceed 30 lines of code**, excluding method definitions, arguments, and local variable declarations. Each line should contain only **one** instruction, with a maximum length of **120 characters** per line. Empty lines, lines containing only curly brackets, and non-code comments do not count towards this limit. However, if a comment contains actual program code, those lines do count.

# Assignment 2. Railway traffic with timetable

Three attributes were added to the file with data on railway stations:  
- **Normal train time** – the time required for a train to travel from one railway station to the next. At the first railway station on the line, this value is 0. When the train travels in the reverse direction, the time must be recalculated based on the last station on the line, which becomes the first station, and its time is 0. For example, on the M501 line, the first station in the normal direction is Kotoriba with 0 minutes. The next station is Donji Kraljevec with 8 minutes, meaning the train needs 8 minutes to travel between these two stations. The penultimate station is Dunjkovec with 6 minutes, and the last station is Macinec with 6 minutes. The train travels 6 minutes between Dunjkovec and Macinec. In the reverse direction, the first station is Macinec, and its time is 0. From Macinec to Dunjkovec, the train travels for 6 minutes. The last station is Kotoriba. From Donji Kraljevec to Kotoriba, the train travels for 8 minutes.  
- **Express train time** – the time required for an express train to travel from one station to the next where the train stops. The express train does not stop at every station along the line (those stations do not have data in this column). At the first station on the line where the train stops, this value is 0. When the train travels in the reverse direction, the time must be recalculated based on the last station on the line where the train stops, which becomes the first station, and its time is 0. For example, on the M201 line in the normal direction, the first express train station is Varaždin with 0 minutes. This means the express train does not travel between Čakovec and Varaždin. The next station where the express train stops is Turčin with 7 minutes, meaning the train travels for 7 minutes between these two stations. The penultimate station is Bedekovčina with 7 minutes, and the last station is Zabok with 10 minutes. The train travels for 10 minutes between Bedekovčina and Zabok. The express train does not stop at any other stations between Zabok and Zaprešić. In the reverse direction, the first station is Zabok with 0 minutes. From Zabok to Bedekovčina, the train travels for 10 minutes. The last station is Varaždin, and from Turčin to Varaždin, the train travels for 7 minutes.  
- **Fast train time** – the time required for a fast train to travel from one station to the next where the train stops. The fast train does not stop at all stations along the line (those stations do not have data in this column). At the first station on the line where the train stops, this value is 0. When the train travels in the reverse direction, the time must be recalculated based on the last station on the line where the train stops, which becomes the first station, and its time is 0. For example, on the R202 line in the normal direction, the first fast train station is Varaždin with 0 minutes. The next station where the fast train stops is Ludbreg with 23 minutes, meaning the train travels for 23 minutes between these two stations. The next and last station where the fast train stops is Koprivnica with 18 minutes. The train travels for 18 minutes between Ludbreg and Koprivnica. In the reverse direction, the first station is Koprivnica with 0 minutes. From Koprivnica to Ludbreg, the train travels for 18 minutes. The last station is Varaždin. From Ludbreg to Varaždin, the train travels for 23 minutes.

![image](https://github.com/user-attachments/assets/192346ec-85b7-4125-ace3-ceecfed8ca63)

![image](https://github.com/user-attachments/assets/0ea44f7f-0d9a-4ca8-bc24-f57a64669ae3)

Within the files from Table 2, NOT all attributes are mandatory! More information about the new files and their attributes can be found in Table 2.

For the timetable, the railway line identifier, direction (N or O), train number, and departure time are mandatory. When the departure station is not listed, the first station on the line in the given direction is taken. When the destination station is not listed, the last station on the line in the given direction is taken. There are three types of trains: normal, express, and fast. When the train type is not specified, it is considered a normal train. The express train type is denoted by "U" and the fast train type by "B." The express train does not stop at every station on the line, but only at those stations listed in the "Express train time" column in the station data file. Similarly, the fast train only stops at stations listed in the "Fast train time" column. For example, train 3801, which operates on line L101 in the normal direction (N), departs at 5:50. Since there is no listed departure station, Mursko Središće is taken as the starting station. Since there is no listed destination station, Čakovec is taken as the destination. Train 3800, operating on line L101 in the reverse direction (O), departs at 5:21. Since there is no listed departure station, Čakovec is taken as the departure station. Since there is no listed destination station, Mursko Središće is taken as the destination. Train 3301, operating on line M501 in the normal direction (N), departs at 4:11. Since there is no listed departure station, Kotoriba is taken as the departure station. The destination station is Čakovec. Train 3901, operating on line M501 in the normal direction (N), departs at 6:34. The departure station is Čakovec, and since there is no listed destination station, Macinec is taken as the destination.

A train can only operate on one line in one direction, so its departure station is the first station on the line in that direction, and its destination station is the last station on the line in that direction, i.e., it operates from the first to the last station on the line in that direction. It has only one stage. For example, this applies to train 3801.

A second train can only operate on one line in one direction, so its departure station is one station on the line, and its destination station is another station on the same line, respecting the direction of the line. It has only one stage. For example, train 3907, operating on line M501 in the normal direction (N), departs at 19:34 from Čakovec and travels to Macinec.

A third train can operate on two lines, so its departure station is one of the stations on the first line, and its destination station is on a different line. This means it has two stages, with the first stage starting at the departure station on the first line and ending at the destination station on the first line. The second stage starts at the departure station on the second line (which is the destination of the first stage) and ends at the destination station on the second line. This train’s departure station is considered to be the departure station on the first stage, and its destination station is the destination station on the second stage. When a train has two stages, they must be arranged chronologically according to the departure times of the stages. For example, train 3800 first operates on line R201 in the reverse direction (O), departing at 5:08 from Varaždin and traveling to Čakovec. The second stage operates on line L101 in the reverse direction (O), departing at 5:21 from Čakovec and traveling to Mursko Središće.

A fourth train can operate on three lines, so its departure station is one of the stations on the first line, it travels part of or the entire second line, and its destination station is on the third line. This means it has three stages, with the first stage starting at the departure station on the first line and ending at the destination station on the first line. The second stage starts at the departure station on the second line (which is the destination of the first stage) and ends at the destination station on the second line. The third stage starts at the departure station on the third line (which is the destination of the second stage) and ends at the destination station on the third line. For this train, the departure station is the one from the first stage, and the destination station is the one from the third stage. When a train has three stages, they must be arranged chronologically based on the departure times of the stages. For example, train 3609 operates first on line R501 in the normal direction (N), departing at 11:56 from Kotoriba and traveling to Čakovec. The second stage operates on line R201 in the normal direction (N), departing at 12:32 from Čakovec and traveling to Varaždin. The third stage operates on line R202 in the normal direction (N), departing at 12:45 from Varaždin and traveling to Koprivnica.

The day designation for the train is used to determine which days of the week the train operates. In the file with day designations, for each day designation, there is a list of days the train operates (Po – Monday, U – Tuesday, Sr – Wednesday, Č – Thursday, Pe – Friday, Su – Saturday, N – Sunday). If the train has a blank designation, it operates on all days of the week (PoUSrČPeSuN). A train can operate on all days of the week (PoUSrČPeSuN). A second train may only operate on weekdays (PoUSrČPe). A third train may only operate on weekends (SuN). A fourth train may operate only on one day (e.g., N). For the purposes of the timetable task, parts of the day designation descriptions that limit the train's operation to a specific period or specify operation or non-operation before, during, or after holidays, etc., are not taken into account. For example, the original designation 9 in the timetable says "Does not operate on Sundays and holidays until June 29 and from September 2. From June 30 to August 31, 2024, operates daily." In the day designation file, designation 9 simply says that the train does not operate on Sundays, so it operates on the following days of the week (PoUSrČPeSu). Another example: the original designation 11 says "Operates daily from June 24 to September 1, 2024. Does not operate on June 30, 2024." In the day designation file, designation 11 states that it operates daily, meaning all days of the week (PoUSrČPeSuN).

For the timetable, the Composite design pattern should be used. The timetable consists of several trains, each with one or more stages, and each stage has its railway stations between the departure and destination stations. All operations related to the trains should use the Composite design pattern without creating any special classes. To enable quicker access when searching for specific railway stations within the stages of trains, an object of a class implementing the Map<K, List<>> interface may be created.

## Expanded Problem Description

**Train Overview**  
- **Syntax:**  
  IV  
- **Example:**  
  IV  
- **Example Description:**  
  Prints a table with trains (train number, departure station, destination station, departure time, arrival time at destination station, total kilometers from the departure station to the destination station of the train).

**Train Stages Overview**  
- **Syntax:**  
  IEV <train number>  
- **Example:**  
  IEV 3609  
- **Example Description:**  
  Prints a table with train stages (train number, line number, stage departure station, stage destination station, stage departure time, stage arrival time, total kilometers from the stage departure station to the stage destination station, days of the week for the stage).

**Overview of Trains Operating All Stages on Specific Weekdays**  
- **Syntax:**  
  IEVD <days>  
- **Example:**  
  IEVD PoSrPeN  
- **Example Description:**  
  Prints a table with trains and their stages that operate on specific weekdays (train number, line number, stage departure station, stage destination station, stage departure time, stage arrival time, days of the week for the stage).

**Train Timetable Overview**  
- **Syntax:**  
  IVRV <train number>  
- **Example:**  
  IVRV 3609  
- **Example Description:**  
  Prints a table with all the railway stations where the train stops (train number, line number, railway station, departure time from the railway station, kilometers from the train's starting station).

**Overview of Trains (Timetable) That Can Be Taken from One Railway Station to Another on a Specific Day Within a Given Time Period**  
- **Syntax:**  
  IVI2S <departure station> - <destination station> - <day> - <from time> - <to time> - <display format>  
- **Examples:**  
  IVI2S Donji Kraljevec - Čakovec - N - 0:00 - 23:59 - SPKV  
  IVI2S Donji Kraljevec - Novi Marof - Pe - 08:00 - 16:00 - KPSV  
  IVI2S Donji Kraljevec - Ludbreg - Su - 5:20 - 20:30 - VSPK  
- **Example Description:**  
  Prints a table with railway stations between two stations, with the number of kilometers, and the train departure times from the railway stations. Only trains operating on a specific day, and whose departure time from the departure station is after the "from time" and arrival time at the destination station is before the "to time," are shown. Data is displayed in columns, the order of which is arbitrary and columns can be repeated. S represents the railway station name, P represents the line, K represents kilometers from the departure railway station, and V represents the departure time of a specific train from the station. The output format may include variations such as SPV (no kilometers shown), KPSVK (kilometers shown in the first and last columns). The train stations are displayed in chronological order of the train's departure time from its departure railway station. For example, the first example shows stations on the same line, the second shows stations on two lines, and the third shows stations on three lines.

**Adding a User to the User Register**  
- **Syntax:**  
  DK <first name> <last name>  
- **Example:**  
  DK Pero Kos  
- **Example Description:**  
  Adds a user named Pero Kos.

**Overview of Users in the User Register**  
- **Syntax:**  
  PK  
- **Example:**  
  PK  
- **Example Description:**  
  Prints a list of users.

**Adding a User to Track Train Travel or Arrival at a Specific Railway Station**  
- **Syntax:**  
  DPK <first name> <last name> - <train number> [- <station>]  
- **Example:**  
  DPK Pero Kos - 3301  
  DPK Mato Medved - 3309 - Donji Kraljevec  
- **Example Description:**  
  Adds user Pero Kos to track train number 3301.  
  Adds user Mato Medved to track train number 3309 at the Donji Kraljevec railway station.

**Simulating Train Travel on a Specific Weekday with a Coefficient for Seconds**  
- **Syntax:**  
  SVV <train number> - <day> - <coefficient>  
- **Example:**  
  SVV 3609 - Po - 60  
- **Example Description:**  
  Simulates train travel from the departure station to the destination station. The simulation time is controlled by a coefficient. If the coefficient is 60, one minute in the simulation lasts 1 real second, meaning the virtual time is 60 times faster than real time. The simulation begins by setting the virtual time to the train’s departure time from the departure station. Then, one virtual minute is executed. If the virtual time matches the time of the next railway station, the train has arrived, and station data (line number, station, time) is printed. All users tracking the train or that specific station are notified. The simulation ends when the train reaches the destination station or if the user enters the "X" symbol in the console, which is checked after every simulated minute.

**Adding Custom Functionality Using the GOF Mediator Pattern**  
- A custom functionality/activity/command must be added to the project using the GOF Mediator pattern. This means documenting a functionality that is not specified in the task description, but will be implemented using the Mediator pattern.

**Running the Program with Data Files**  
- When running the program, arguments/options and filenames with data are entered in a single line. For example:  
  > java -jar /home/UzDiz/DZ_2/sarbutina20_zadaca_2/target/sarbutina20_zadaca_2.jar --zs DZ_2_stanice.csv --zps DZ_2_vozila.csv --zk DZ_2_kompozicije.csv --zvr DZ_2_vozni_red.csv --zod DZ_2_oznake_dana.csv
 
# Assignment 3. Railway Traffic with timetable  

It is necessary to add functionality for purchasing passenger tickets. The determination of the passenger train fare is based on €/km for each type of train (regular, accelerated, fast). The base ticket price applies to purchases at the ticket counter. The company offers a % discount on the price if traveling by train on Saturdays and/or Sundays. The company wants to promote ticket purchases through the web/mobile application, for which it offers a certain % discount on the price. On the other hand, the company sets a % increase in the price for ticket purchases on the train. The calculation of the ticket price based on the purchase method (counter, web/mobile application, on the train) should be based on the Strategy design pattern.
Each ticket purchase must be stored so that it can be accessed later, and it should be based on the Memento design pattern.
  
On the railway infrastructure, it is necessary to perform work on tracks and their rails as preventive maintenance or to fix faults. Work is usually not performed on the entire track but between two stations, which is why the status of the track on that route (between two stations) needs to be changed. The track statuses are: I - operational, K - faulty, T - testing, Z - closed. If there is only one rail on a specific track route (between two stations), then the status change affects both directions (normal and reverse). For example, on the M501 track for the stations Donji Mihaljevec – Čehovec, it refers to the normal direction, and Čehovec - Donji Mihaljevec to the reverse direction. If the track status is changed to indicate that it is faulty, it means that trains cannot travel on that track between those two stations because there is only one rail. If a track between two stations is set to closed, that route must undergo testing to be able to receive the status of being operational. On the same track, status changes can occur between multiple routes (between two stations), but the routes cannot intersect. For example, on the M501 track, the status can be set to faulty for the route between the stations Donji Mihaljevec - Čehovec. Then, on the same M501 track, the status can be set to faulty for the route between the stations Čehovec – Mala Subotica. However, on the same M501 track, the status cannot be set to faulty for the route between the stations Donji Kraljevec – Mala Subotica because the station Donji Kraljevec is located on the same track between the stations Donji Mihaljevec and Čehovec, which has already been set to faulty. Logically, on the same M501 track, the status cannot be set to faulty for the route between the stations Mala Subotica - Donji Kraljevec because the station Donji Kraljevec is located between the stations Donji Mihaljevec and Čehovec, for which the route has already been set to faulty, and the track on that section has only one rail.
  
If there are two rails on a specific track route (between two stations), then the status change only affects the direction (normal or reverse) determined by the order of the stations. This means that trains can travel on that track between those two stations in the opposite direction. For example, if on the M101 track for the route between the stations Podsused – Gajnice the status is set to faulty, it means that the track is faulty for that route in the normal direction, but the track on the same route in the reverse direction (Gajnice – Podsused) is operational. It is possible on the M101 track for the route between the stations Vrapče - Zaprešić to set the status to faulty, which means that the track is faulty for that route in the reverse direction. In this case, there is an allowed intersection because the route Gajnice – Podsused is within the route Zaprešić – Vrapče, but the route Vrapče – Zaprešić is in the reverse direction. On the other hand, when considering the system's functioning, this is a case where trains could not travel on the M101 track in the intersection of routes for both directions because there are faults on both rails, which in this case is the route Podsused – Gajnice for the normal direction and the route Gajnice – Podsused for the reverse direction.
  
Work with tracks and their statuses should be based on the State design pattern. Changing the track status when it is not operational affects the timetable and, consequently, ticket purchases.

## Expanded Problem Description

### **Determining Passenger Train Fare (€/km), Discounts for Saturday and Sunday, % Discount for Web/Mobile App Purchases, and % Increase for On-Train Purchases**
   - **Syntax:**
     ```
     CVP priceRegular priceAccelerated priceFast discountSatSun discountWebMob increaseTrain
     ```
   - **Example:**
     ```
     CVP 0.10 0.12 0.15 20.0 10.0 10.0
     ```
   - **Example Description:**
     - Ticket price for a regular train is €0.10/km, for an accelerated train is €0.12/km, for a fast train is €0.15/km.
     - Discount for traveling on Saturday and Sunday is 20.0%.
     - Discount for purchasing via web/mobile app is 10.0%.
     - Increase for purchasing on the train is 10.0%.

### **Purchasing a Ticket for Travel Between Two Stations on a Specific Date with a Chosen Purchase Method**
   - **Syntax:**
     ```
     KKPV2S trainID - departureStation - destinationStation - date - purchaseMethod
     ```
   - **Example:**
     ```
     KKPV2S 3609 - Donji Kraljevec - Čakovec - 10.01.2025 - WM
     ```
   - **Example Description:**
     - Purchase a ticket for train 3609 on the route Donji Kraljevec - Čakovec for 10.01.2025, purchased via web/mobile app.
     - Other purchase methods: B (counter), V (train).
     - The ticket must include: train details, route, date, departure and arrival times, base price, discounts, final price, purchase method, and purchase date/time.

### **Inspection of Purchased Train Tickets**
   - **Syntax:**
     ```
     IKKPV [n]
     ```
   - **Example 1:**
     ```
     IKKPV
     ```
     - **Description:** Inspect all purchased train tickets.
   - **Example 2:**
     ```
     IKKPV 3
     ```
     - **Description:** Inspect the 3rd purchased train ticket.

### **Comparison of Tickets for Travel Between Two Stations on a Specific Date Within a Given Time Frame and Purchase Method**
   - **Syntax:**
     ```
     UKP2S departureStation - destinationStation - date - startTime - endTime - purchaseMethod
     ```
   - **Example 1:**
     ```
     UKP2S Donji Kraljevec - Čakovec - 10.01.2025 - 0:00 - 23:59 - WM
     ```
     - **Description:** Compare tickets for travel on the route Donji Kraljevec - Čakovec on 10.01.2025, departing after 0:00 and arriving before 23:59, purchased via web/mobile app.
   - **Example 2:**
     ```
     UKP2S Donji Kraljevec - Novi Marof - 10.01.2025 - 08:00 - 16:00 - B
     ```
     - **Description:** Compare ticket prices for travel on the route Donji Kraljevec - Novi Marof on 10.01.2025, departing after 8:00 and arriving before 16:00, purchased at the counter.
   - **Example 3:**
     ```
     UKP2S Donji Kraljevec - Ludbreg - 10.01.2025 - 5:20 - 20:30 - V
     ```
     - **Description:** Compare ticket prices for travel on the route Donji Kraljevec - Ludbreg on 10.01.2025, departing after 5:20 and arriving before 20:30, purchased on the train.

### **Changing the Status of a Track Between Two Stations**
   - **Syntax:**
     ```
     PSP2S trackID - departureStation - destinationStation - status
     ```
   - **Example:**
     ```
     PSP2S M501 - Donji Kraljevec - Mala Subotica - K
     ```
   - **Example Description:**
     - Change the status of track M501 on the route Donji Kraljevec - Mala Subotica to "faulty" (K), meaning trains cannot travel on this route due to a single rail.
     - Other statuses: I (operational), T (testing), Z (closed).

### **Inspection of Track Routes with a Specific Status**
   - **Syntax:**
     ```
     IRPS status [trackID]
     ```
   - **Example 1:**
     ```
     IRPS K
     ```
     - **Description:** Inspect all routes with a "faulty" status on all tracks.
   - **Example 2:**
     ```
     IRPS Z M501
     ```
     - **Description:** Inspect all routes with a "closed" status on track M501.

### **Adding Custom Functionality Using the Command Design Pattern**
   - It is necessary to add custom functionality (activity/command) to the project using the **Command** design pattern (GoF). This means the project documentation must include a description of the functionality not specified in the task description, which will be implemented using the Command pattern.
