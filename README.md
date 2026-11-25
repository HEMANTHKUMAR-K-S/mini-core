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



## 25/11/2026


# miniCore – Asynchronous Check-In & Check-Out Batch Processing

## 1. Overview

This module implements asynchronous batch processing for Check-In and Check-Out records using Spring Boot, Spring Integration, Spring Batch, and dedicated thread pools.

The application periodically checks the database for pending Check-In and Check-Out records.

When records are available:

1. The database poller detects pending records.
2. A worker is submitted to the corresponding thread pool.
3. The worker claims a configured number of records.
4. A unique transaction ID (`txId`) is assigned to the claimed records.
5. The worker launches the corresponding Spring Batch Job asynchronously.
6. The Tasklet processes records associated with that `txId`.
7. After successful Job completion, the JobListener deletes the processed records.

Check-In and Check-Out processing are independent and use separate thread pools and Batch Jobs.

---

## 2. Architecture

The application contains two independent processing pipelines.

### Check-In Pipeline

```text
Check-In Database Poller
          |
          v
Check-In Service Activator
          |
          v
Check-In Worker Executor
          |
          v
Check-In Thread Pool
          |
          v
Check-In Worker
          |
          v
Record Claim Service
          |
          v
Fetch configured number of records
          |
          v
Generate unique txId
          |
          v
Check-In Job Operator
          |
          v
Check-In Batch Job
          |
          v
Check-In Step
          |
          v
Check-In Tasklet
          |
          v
Process claimed records
          |
          v
Check-In Job Listener
          |
          v
Delete records belonging to txId
```

### Check-Out Pipeline

```text
Check-Out Database Poller
          |
          v
Check-Out Service Activator
          |
          v
Check-Out Worker Executor
          |
          v
Check-Out Thread Pool
          |
          v
Check-Out Worker
          |
          v
Record Claim Service
          |
          v
Fetch configured number of records
          |
          v
Generate unique txId
          |
          v
Check-Out Job Operator
          |
          v
Check-Out Batch Job
          |
          v
Check-Out Step
          |
          v
Check-Out Tasklet
          |
          v
Process claimed records
          |
          v
Check-Out Job Listener
          |
          v
Delete records belonging to txId
```

---

## 3. Database Polling

The application uses Spring Integration JDBC inbound channel adapters to periodically execute database queries.

Two separate pollers are configured:

- Check-In Poller
- Check-Out Poller

The pollers retrieve the number of pending records from the database.

Example Check-In query:

```sql
SELECT COUNT(*)
FROM attendance_log
WHERE status = 'CHECK-IN';
```

Example Check-Out query:

```sql
SELECT COUNT(*)
FROM attendance_log
WHERE status = 'CHECK-OUT';
```

The poller sends the query result to its respective Service Activator.

### Service Activator Responsibility

The Service Activator:

1. Receives the database count.
2. Checks whether the count is greater than zero.
3. Submits the corresponding worker to its thread pool.

If the count is zero, no worker is submitted.

---

## 4. Separate Thread Pool Configuration

Check-In and Check-Out use separate ThreadPoolTaskExecutor instances.

### Check-In Thread Pool

Bean name:

```java
checkInThreadPool
```

Thread name prefix:

```text
CheckInPool-
```

### Check-Out Thread Pool

Bean name:

```java
checkOutThreadPool
```

Thread name prefix:

```text
CheckOutPool-
```

Each pool is responsible for its respective worker and asynchronous Batch Job execution.

The JobOperator uses the corresponding TaskExecutor to execute its submitted jobs.

### Why separate thread pools?

- Check-In processing does not occupy Check-Out worker capacity.
- Check-Out processing does not occupy Check-In worker capacity.
- Both pipelines can execute independently.
- Thread execution can be monitored through thread names in application logs.

---

## 5. Worker Implementation

There are two workers:

- CheckInWorker
- CheckOutWorker

Their responsibilities are:

1. Request records from RecordClaimService.
2. Claim records that have not already been assigned a transaction ID.
3. Generate a unique transaction ID.
4. Pass the transaction ID as a Spring Batch Job Parameter.
5. Submit the corresponding Batch Job asynchronously.

Example:

```java
JobParameters jobParameters =
        new JobParametersBuilder()
                .addString("txId", txId)
                .toJobParameters();
```

The transaction ID identifies the records belonging to a particular batch.

The worker does not wait for the Tasklet to finish. It submits the Job and returns.

---

## 6. Record Claiming Using txId

A unique transaction ID is generated for every claimed batch.

Example:

Suppose the database contains 120 pending Check-In records and the configured batch size is 50.

The records are divided as follows:

| Batch | Records | txId |
|---|---:|---|
| Batch 1 | 50 | TX1001 |
| Batch 2 | 50 | TX1002 |
| Batch 3 | 20 | TX1003 |

All records belonging to one batch share the same transaction ID.

### Why use txId?

- Identifies records belonging to a specific batch.
- Prevents different workers from processing already claimed records.
- Allows the Tasklet to fetch only its assigned records.
- Allows the JobListener to delete only the records belonging to its completed batch.
- Supports independent processing of multiple batches.

The claim operation is transactional.

The records are selected, assigned a transaction ID, and saved within the claim transaction.

---

## 7. Configurable Batch Size

The batch size is maintained in application configuration.

Example:

```yaml
batch:
  processing:
    job-size: 50
```

The application reads this property through JobProperties.

The RecordClaimService uses this value to determine how many records should be fetched for each batch.

If the configured batch size is 50, each worker claims up to 50 eligible records.

---

## 8. Spring Batch Job Configuration

Each processing pipeline has its own Spring Batch Job.

### Check-In Job

```text
checkInJob
    |
    v
checkInStep
    |
    v
CheckInTasklet
```

### Check-Out Job

```text
checkOutJob
    |
    v
checkOutStep
    |
    v
CheckOutTasklet
```

Both jobs use Tasklet-based processing instead of chunk-oriented processing.

### Why Tasklet?

The requirement is to process a group of already claimed records and then delete those records after successful Job completion.

The Tasklet approach provides a simple unit of work for each batch.

---

## 9. Tasklet Processing

The Tasklet receives the `txId` through Job Parameters.

Example:

```java
String txId = (String) chunkContext
        .getStepContext()
        .getJobParameters()
        .get("txId");
```

It then retrieves the records belonging to that transaction ID.

```java
List<UserLogs> records =
        userLogRepo.findAllByTxId(txId);
```

The Tasklet processes the retrieved records and returns:

```java
return RepeatStatus.FINISHED;
```

The Check-In and Check-Out Tasklets follow the same approach but process their respective batches.

---

## 10. JobExecutionListener

Each Batch Job has its own listener.

- CheckInJobListener
- CheckOutJobListener

The listener executes after the Batch Job finishes.

### Successful Execution

If the Job status is COMPLETED:

1. Retrieve the Job Parameter `txId`.
2. Delete records belonging to that transaction ID.
3. Log the number of deleted records.

Example:

```java
if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

    int deletedCount =
            userLogRepo.deleteAllByTxId(txId);

    log.info("Deleted records: {}", deletedCount);
}
```

### Failed Execution

If the Job fails:

- Records are retained.
- The listener does not delete the claimed records.
- They remain available for a separate recovery mechanism.

This prevents deletion of records when processing has not completed successfully.

---

## 11. JobOperator and JobRegistry

The application uses TaskExecutorJobOperator for asynchronous Job execution.

Two JobOperators are configured:

| JobOperator | TaskExecutor |
|---|---|
| checkInJobOperator | checkInThreadPool |
| checkOutJobOperator | checkOutThreadPool |

Both operators share:

- JobRepository
- JobRegistry

### JobRepository

Maintains Spring Batch execution metadata, including:

- Job instances
- Job executions
- Step executions
- Execution status
- Job parameters

### JobRegistry

Maintains registered Spring Batch jobs.

The Check-In and Check-Out Jobs are registered in the shared registry.

### TaskExecutorJobOperator

Submits jobs for execution using the configured TaskExecutor.

The worker returns after job submission rather than waiting for the Tasklet to complete.

---

## 12. Complete Execution Example

Assume the database contains:

- 120 pending Check-In records.
- 80 pending Check-Out records.
- Configured batch size: 50.

### Check-In Processing

| Batch | Records | Transaction ID |
|---|---:|---|
| 1 | 50 | TX1001 |
| 2 | 50 | TX1002 |
| 3 | 20 | TX1003 |

### Check-Out Processing

| Batch | Records | Transaction ID |
|---|---:|---|
| 1 | 50 | TX2001 |
| 2 | 30 | TX2002 |

Each batch is independently claimed and submitted to its corresponding Batch Job.

After successful processing, its listener deletes only the records associated with that batch's transaction ID.

---

## 13. Logging and Monitoring

The application logs important events throughout the processing lifecycle.

Examples:

```text
Total check-ins: 120

Submitting Check-In worker
Check-In worker started with txId: TX1001

Processing 50 Check-In records
Check-In Job completed

Deleted records for txId: TX1001
```

Separate thread prefixes help identify which thread pool is executing the work.

```text
CheckInPool-
CheckOutPool-
```

---

## 14. Important Design Considerations

1. Check-In and Check-Out processing are independent.
2. Each batch receives a unique transaction ID.
3. Records are claimed transactionally.
4. Tasklets process only records assigned to their transaction ID.
5. Job execution is asynchronous.
6. Records are deleted only after successful Job completion.
7. Failed batches retain their records for recovery.
8. JobRepository maintains execution metadata separately from the application's business records.

---

## 15. Future Enhancement

Planned enhancement:

**Spring Retry Integration**

The next improvement is to introduce configurable exception-specific retry policies.

The intended functionality:

- Define retryable exception classes in application configuration.
- Configure maximum attempts for each exception.
- Configure retry delay.
- Retry only the affected batch using its existing transaction ID.
- Retain records when all retry attempts fail.

---

## 16. Summary

Implemented asynchronous Check-In and Check-Out batch processing in miniCore using Spring Integration and Spring Batch.

The solution uses separate workers, thread pools, Tasklet-based Jobs, unique transaction IDs, and JobExecutionListeners.

The implementation supports independent batch execution and ensures that records are deleted only after successful processing.
