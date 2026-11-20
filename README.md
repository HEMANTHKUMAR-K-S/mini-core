# miniCore – CSV File Processing with Spring Integration and Spring Batch

## Overview

miniCore is a Spring Boot application that integrates **Spring Integration** and **Spring Batch** to automate CSV file processing.

Whenever a CSV file is dropped into the configured input directory, the application detects the file, validates it, waits until the file is completely copied, and launches a Spring Batch job to process its records using chunk-oriented processing.

The processed records are stored in a PostgreSQL database. After successful processing, the original CSV file is backed up.

## Technologies Used

- Java 27
- Spring Boot 4.1.2
- Spring Integration
- Spring Batch 6
- Spring Data JPA
- Hibernate
- PostgreSQL
- Gradle
- IntelliJ IDEA

## Architecture

```text
             CSV Input Folder
                     |
                     v
              File Watcher
                     |
                     v
              FileSystemEvent
                     |
                     v
             Spring Integration
                IntegrationFlow
                     |
                     v
              File Validation
                     |
                     v
             Duplicate Filter
                     |
                     v
              File Stability
                  Check
                     |
                     v
            BatchJobLauncherService
                     |
                     v
              Spring Batch Job
                  userJob
                     |
             +-------+-------+
             |               |
             v               v
          userStep        backupStep
             |               |
             v               v
           Reader          Tasklet
             |               |
             v               v
          Processor       File Backup
             |
             v
           Writer
             |
             v
        PostgreSQL
          users
                     |
                     v
           Job Completion
               Listener
```

## Key Features

### 1. Automatic File Detection

- Monitors the configured input directory.
- Detects new CSV files using `ENTRY_CREATE`.
- Detects modifications to existing files using `ENTRY_MODIFY`.
- Ignores unsupported file types.

### 2. File Validation

- Accepts only `.csv` files.
- Ignores directories.
- Uses a duplicate filter to prevent simultaneous or repeated processing of unchanged files.

### 3. File Stability Check

- Checks file size and last-modified timestamp.
- Waits until the file becomes stable.
- Prevents reading incomplete files during file copying.

### 4. Spring Batch Processing

Uses chunk-oriented processing to efficiently import CSV records.

The Batch job consists of:

**Reader**
- Reads CSV records using `FlatFileItemReader`.
- Maps CSV columns to the `CsvRecords` model.
- Skips the header row.

**Processor**
- Validates incoming records.
- Converts `CsvRecords` objects into `User` entities.

**Writer**
- Writes processed records using Spring Data JPA.
- Uses `UserRepository` to persist records.

**Chunk Configuration**
- Configured chunk size: 2.
- Processes records in groups of two.
- Uses transaction management for chunk writes.

### 5. Automatic File Backup

- Executes after successful database import.
- Uses a Tasklet for the backup operation.
- Uses a temporary `.part` file during backup.
- Attempts atomic file movement where supported.

### 6. Job Execution Listener

Uses `FileJobCompletionListener` to handle the final processing state.

- Marks the file as processed when the job completes successfully.
- Releases the file for retry when the job fails.

### 7. Batch Metadata Management

Spring Batch maintains execution information in its metadata tables, including:

- `BATCH_JOB_INSTANCE`
- `BATCH_JOB_EXECUTION`
- `BATCH_STEP_EXECUTION`

These tables track job instances, execution status, and step execution details.

## CSV File Format

The application expects the following CSV structure:

```csv
id,name,age,location,salary
101,Rahul,25,Bangalore,35000
102,Divya,28,Chennai,45000
103,Arun,30,Hyderabad,50000
104,Priya,24,Mumbai,32000
105,Kiran,27,Pune,40000
```

### Field Description

| Field | Data Type |
|---|---|
| id | Integer |
| name | String |
| age | Integer |
| location | String |
| salary | Double |


## Execution Flow

1. Start the miniCore Spring Boot application.
2. The file watcher monitors the configured input directory.
3. Drop a CSV file into the input directory.
4. Spring Integration detects the file event.
5. The application validates the file and checks for duplicates.
6. The file stability service waits until copying is complete.
7. `BatchJobLauncherService` launches the `userJob`.
8. `userStep` reads, validates, and writes CSV records in chunks.
9. After successful import, `backupStep` backs up the original CSV.
10. `FileJobCompletionListener` updates the file-processing status.

## Configuration

Configure the following application settings according to your environment:

- Database connection properties.
- CSV input directory.
- CSV backup directory.
- File stability check interval.
- File stability timeout.
- Spring Batch metadata initialization.

Spring Batch jobs are launched when files arrive rather than automatically at application startup.

[//]: # (## Running the Application)

[//]: # (Clone the repository:)

[//]: # (```bash)

[//]: # (git clone <YOUR_GITHUB_REPOSITORY_URL>)

[//]: # (```)

[//]: # (Navigate to the project directory:)

[//]: # (```bash)

[//]: # (cd miniCore)

[//]: # (```)
[//]: # ()
[//]: # (Build the project:)

[//]: # ()
[//]: # (```bash)

[//]: # (./gradlew clean build)

[//]: # (```)

[//]: # ()
[//]: # (On Windows:)

[//]: # ()
[//]: # (```powershell)

[//]: # (.\gradlew.bat clean build)

[//]: # (```)

[//]: # ()
[//]: # (Run the application:)

[//]: # ()
[//]: # (```powershell)

[//]: # (.\gradlew.bat bootRun)

[//]: # (```)

[//]: # ()
[//]: # (Alternatively, run `MiniCoreApplication` from IntelliJ IDEA.)

## Testing

Test the application using these scenarios:

1. Drop a valid CSV file and verify database insertion.
2. Verify chunk processing.
3. Verify backup after successful execution.
4. Drop an unsupported file and verify it is ignored.
5. Drop an invalid CSV and verify failure handling.
6. Submit an unchanged file again and verify duplicate filtering.
7. Modify an existing CSV and verify that the watcher detects the modification.

## Future Enhancements

- Incremental CSV processing using persistent checkpoints.
- Process only newly appended CSV records.
- Improve duplicate handling across application restarts.
- Add more advanced retry and skip strategies.
- Improve concurrent file-processing support.
- Add detailed file-processing audit logs.

## Learning Objectives

This project demonstrates practical implementation of:

- Spring Integration file monitoring.
- Message channels and IntegrationFlow.
- Event-driven application architecture.
- Spring Batch job and step configuration.
- Chunk-oriented processing.
- ItemReader, ItemProcessor, and ItemWriter.
- JobParameters and JobRepository.
- Tasklet-based file operations.
- Transaction management.
- File validation and duplicate handling.



## Database Polling with Spring Integration

### Overview

Implemented separate database polling mechanisms for Check-In and Check-Out operations using Spring Integration JDBC Polling Adapters.

Both pollers execute independently at a fixed interval of 5 seconds, retrieve attendance counts from the `attendance_log` table, and send the results to their respective Service Activators through separate DirectChannels.

### Architecture

```text
            
             |                   |
          Check-In           Check-Out 
             |                   |
         CheckIn SA          CheckOut SA
             |                   |
             +---------+---------+
                       |
                user_logs table
                       |
             +---------+---------+
             |                   |
       Check-In Poller     Check-Out Poller
             |                   |
       COUNT(CHECK-IN)     COUNT(CHECK-OUT)
             |                   |
      checkInCountChannel  checkOutCountChannel
             |                   |
       Check-In SA         Check-Out SA
             |                   |
       Log total count     Log total count
```

### Components

**1. JDBC Polling Adapters**

- Uses `JdbcPollingChannelAdapter` to execute database queries.
- Check-In poller retrieves the total number of Check-In records.
- Check-Out poller retrieves the total number of Check-Out records.
- Both pollers execute every 5 seconds using `Pollers.fixedDelay(5000)`.

**2. Message Channels**

Created two independent `DirectChannel` beans:

- `checkInCountChannel`
- `checkOutCountChannel`

These channels transfer polling results to their respective Service Activators.

**3. Service Activators**

Implemented two Service Activators:

- `CheckInPollerServiceActivator`
- `CheckoutPollerServiceActivator`

Each Service Activator receives a `List<Long>` containing the JDBC query results, extracts the first element using `get(0)`, and logs the total count.

### Database Queries

Check-In:

```sql
SELECT COUNT(*) AS total
FROM user_logs
WHERE status = 'CHECK-IN';
```

Check-Out:

```sql
SELECT COUNT(*) AS total
FROM user_logs
WHERE status = 'CHECK-OUT';
```

### Execution Flow

1. The JDBC Polling Adapter executes its respective SQL query every 5 seconds.
2. The RowMapper converts the query result into a Java `Long`.
3. Spring Integration creates a message containing the query result.
4. The message is sent to the corresponding DirectChannel.
5. The Service Activator receives the message payload.
6. The total count is extracted and printed using application logging.

### Expected Logs

```text
Total check-ins : 5
Total check-outs : 3
```

### Key Concepts Learned

- JDBC Polling Adapter
- Spring Integration Message and Payload
- DirectChannel
- Service Activator
- Polling with fixed delay
- Database-to-channel communication
- Message payload handling
