package com.example.kinesisdemo.util;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.kinesis.retrieval.AggregatorUtil;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecordDeaggregator<T> {

    /**
     * Interface used by a calling method to call the process function
     *
     */
    public interface KinesisUserRecordProcessor {
        public Void process(List<KinesisClientRecord> userRecords);
    }

    private Record convertOne(KinesisEvent.KinesisEventRecord record) {
        KinesisEvent.Record r = record.getKinesis();
        Record out = Record.builder().partitionKey(r.getPartitionKey()).encryptionType(r.getEncryptionType())
                .approximateArrivalTimestamp(r.getApproximateArrivalTimestamp().toInstant())
                .sequenceNumber(r.getSequenceNumber()).data(SdkBytes.fromByteBuffer(r.getData())).build();

        return out;

    }

    private List<KinesisClientRecord> convertToKinesis(List<KinesisEventRecord> inputRecords) {
        List<KinesisClientRecord> response = new ArrayList<>();

        inputRecords.stream().forEachOrdered(record -> {
            response.add(KinesisClientRecord.fromRecord(convertOne(record)));
        });

        return response;

    }

    @SuppressWarnings("unchecked")
    private List<KinesisClientRecord> convertType(List<T> inputRecords) throws Exception {
        List<KinesisClientRecord> records = null;

        if (inputRecords.size() > 0 && inputRecords.get(0) instanceof KinesisEventRecord) {
            records = convertToKinesis((List<KinesisEventRecord>) inputRecords);
        } else if (inputRecords.size() > 0 && inputRecords.get(0) instanceof Record) {
            records = new ArrayList<>();
            for (Record rec : (List<Record>) inputRecords) {
                records.add(KinesisClientRecord.fromRecord((Record) rec));
            }
        } else {
            if (inputRecords.size() == 0) {
                return new ArrayList<KinesisClientRecord>();
            } else {
                throw new Exception("Input Types must be Kinesis Event or Model Records");
            }
        }

        return records;
    }

    /**
     * Method to process a set of Kinesis user records from a Stream of Kinesis
     * Event Records using the Java 8 Streams API
     *
     * @param inputStream    The Kinesis Records provided by AWS Lambda or the
     *                       Kinesis SDK
     * @param streamConsumer Instance implementing the Consumer interface to process
     *                       the deaggregated UserRecords
     * @return Void
     */
    public Void stream(Stream<T> inputStream, Consumer<KinesisClientRecord> streamConsumer) throws Exception {
        // deaggregate UserRecords from the Kinesis Records

        List<T> streamList = inputStream.collect(Collectors.toList());
        List<KinesisClientRecord> deaggregatedRecords = new AggregatorUtil().deaggregate(convertType(streamList));
        deaggregatedRecords.stream().forEachOrdered(streamConsumer);

        return null;
    }

    /**
     * Method to process a set of Kinesis user records from a list of Kinesis
     * Records using pre-Streams style API
     *
     * @param inputRecords The Kinesis Records provided by AWS Lambda
     * @param processor    Instance implementing KinesisUserRecordProcessor
     * @return Void
     */
    public Void processRecords(List<T> inputRecords, KinesisUserRecordProcessor processor) throws Exception {
        // invoke provided processor
        return processor.process(new AggregatorUtil().deaggregate(convertType(inputRecords)));
    }

    /**
     * Method to bulk deaggregate a set of Kinesis user records from a list of
     * Kinesis Event Records.
     *
     * @param inputRecords The Kinesis Records provided by AWS Lambda
     * @return A list of Kinesis UserRecord objects obtained by deaggregating the
     *         input list of KinesisEventRecords
     */
    public List<KinesisClientRecord> deaggregate(List<T> inputRecords) throws Exception {
        List<KinesisClientRecord> outputRecords = new LinkedList<>();
        outputRecords.addAll(new AggregatorUtil().deaggregate(convertType(inputRecords)));

        return outputRecords;
    }

    /**
     * Method to deaggregate a single Kinesis record into a List of UserRecords
     *
     * @param inputRecord The Kinesis Record provided by AWS Lambda or Kinesis SDK
     * @return A list of Kinesis UserRecord objects obtained by deaggregating the
     *         input list of KinesisEventRecords
     */
    public List<KinesisClientRecord> deaggregate(T inputRecord) throws Exception {
        return new AggregatorUtil().deaggregate(convertType(Arrays.asList(inputRecord)));
    }
}
