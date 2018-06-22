package edu.upenn.cis.precise.openicelite.iomt.medical.masimo.rad8;

import edu.upenn.cis.precise.openicelite.iomt.api.DeviceInfo;
import edu.upenn.cis.precise.openicelite.iomt.api.IDriver;
import edu.upenn.cis.precise.openicelite.iomt.api.IDriverCallback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import purejavacomm.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Enable an application to communicate with a Masimo Rad-8 Pulse Oximeter
 * <p>
 * This driver doesn't support single read/write operations. It only provides subscribe
 * capability to continuously read data from serial port and call corresponding IDriverCallback
 * handler.
 *
 * @author Pruthvi Hanumanthapura Ramakrishna (hrama@seas.upenn.edu)
 * @author Hung Nguyen (hungng@seas.upenn.edu)
 */
public class MasimoDriver implements IDriver, SerialPortEventListener {
    private static final String DEFAULT_DEVICE_ID = "b0d9491a-5001-480c-86af-5f35dc8eb286";

    private static final Logger logger = LogManager.getLogger(MasimoDriver.class);

    // Device information
    private DeviceInfo info = new DeviceInfo(DEFAULT_DEVICE_ID);

    // Serial port
    private boolean foundPort = false;
    private CommPortIdentifier portId;
    private SerialPort port;
    private BufferedReader reader;

    // Running configuration
    // -- serial connection retry interval (seconds)
    private int retryInterval = 5;

    private IDriverCallback callback;
    private AtomicBoolean isConnected = new AtomicBoolean(false);   // Indicate serial port is ready
    private AtomicBoolean isRunning = new AtomicBoolean(false);     // Indicate driver is reading from serial
    private AtomicBoolean isClosing = new AtomicBoolean(false);     // Should not retry if we're closing

    public MasimoDriver(String portName) {
        // Try to find serial port
        logger.info("Detecting port " + portName + "...");

        while (!foundPort) {
            try {
                Enumeration ports = CommPortIdentifier.getPortIdentifiers();

                LinkedList<String> portNames = new LinkedList<>();
                while (ports.hasMoreElements() && !foundPort) {
                    portId = (CommPortIdentifier) ports.nextElement();
                    portNames.add(portId.getName());
                    if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL
                            && portId.getName().equalsIgnoreCase(portName)) {
                        foundPort = true;
                        isConnected.set(true);
                        logger.info("Found port!");
                    }
                }

                if (!foundPort) {
                    logger.info("Cannot find port. Available ports: " + String.join(",", portNames));
                    logger.info("Retrying in " + retryInterval + " seconds...");
                    Thread.sleep(retryInterval * 1000);
                }
            } catch (Exception ex) {
                logger.error("Exception during detecting port!", ex);
            }
        }
    }

    /**
     * Configure device with attributes (e.g., device ID, type)
     *
     * @param info attributes as a DeviceInfo object
     */
    @Override
    public void setDeviceInfo(DeviceInfo info) {
        this.info = info;
    }

    /**
     * Return device information
     *
     * @return attributes as a DeviceInfo object
     */
    @Override
    public DeviceInfo getDeviceInfo() {
        return info;
    }

    /**
     * Perform some initializing tasks if needed
     *
     * @param options initializing options
     */
    @Override
    public void init(HashMap<String, Object> options) {
        if (options != null) {
            if (options.containsKey("retry_interval")) {
                retryInterval = (int) options.get("retry_interval");
            }
        }
    }

    /**
     * Initiate a connection to a device
     * This function is blocking but driver must return immediately after
     * success or error. Any background tasks to maintain alive connection must
     * be running on separate threads.
     *
     * @param address device's serial/ethernet address
     * @param name    identified client name (must be unique within the system)
     * @param options additional connecting options
     */
    @Override
    public void connect(String address, String name, HashMap<String, Object> options) {
        // do nothing
    }

    /**
     * This device doesn't support single read operation
     *
     * @param options additional options
     * @return a message as string
     */
    @Override
    public String read(HashMap<String, Object> options) {
        return null;
    }

    /**
     * Blocking read a message from connected device
     *
     * @param options additional options
     * @return a message as byte array
     */
    @Override
    public byte[] readBytes(HashMap<String, Object> options) {
        return new byte[0];
    }

    /**
     * This device doesn't support single write operation
     *
     * @param message a message as string
     */
    @Override
    public void write(String message) {

    }

    /**
     * This device doesn't support single write operation
     *
     * @param message a message as byte array
     */
    @Override
    public void write(byte[] message) {

    }

    /**
     * Subscribe to the device events (e.g., periodic reported data)
     *
     * @param options  additional options
     * @param callback the class to callback for related events
     */
    @Override
    public void subscribe(HashMap<String, Object> options, IDriverCallback callback) {
        if (!foundPort) throw new IllegalStateException("Serial Port is not found!");
        if (isRunning.get()) return;

        this.callback = callback;
        while (!isRunning.get()) {
            try {
                Thread.sleep(retryInterval * 1000);
                logger.info("Opening port...");
                port = (SerialPort) portId.open(info.getDeviceId(), 10000);
                port.setSerialPortParams(9600, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                port.addEventListener(this);
                port.notifyOnDataAvailable(true);
                reader = new BufferedReader(new InputStreamReader(port.getInputStream(), MasimoConstants.CHARSET),
                        1000);
                isRunning.set(true);
                logger.info("Port is opened! Listening for data...");
            } catch (Exception ex) {
                isRunning.set(false);
                logger.error("Cannot open port " + portId.getName(), ex);
            }
        }
    }

    /**
     * Unsubscribe to the device events
     */
    @Override
    public void unsubscribe() {
        if (isRunning.get()) {
            logger.info("Closing port...");
            isRunning.set(false);
            port.close();
        }
    }

    /**
     * Disconnect from device
     */
    @Override
    public void disconnect() {
        unsubscribe();
    }

    /**
     * Close and release all associated resource
     */
    @Override
    public void close() {
        isClosing.set(true);
        disconnect();
        isConnected.set(false);
        foundPort = false;
        port = null;
        portId = null;
    }

    /**
     * Determine if client is currently connect to a device
     *
     * @return true if connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        return isConnected.get();
    }

    /**
     * Set the callback listener to use for events that happen asynchronously
     *
     * @param callback the class to callback for related events
     */
    @Override
    public void setCallback(IDriverCallback callback) {
        this.callback = callback;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String data = reader.readLine().trim();
                if (data.length() > 0) {
                    callback.handleMessage(data);
                }
            } catch (Exception ex) {
                // The driver must provide fault-tolerance such that it will retry if an
                // exception occur during handling events
                logger.error("Failed to handle serial event!", ex);
                try {
                    unsubscribe();
                } catch (Exception e) {
                    // ignore
                }

                if (!isClosing.get()) {
                    logger.info("Retrying in " + retryInterval + " seconds...");
                    subscribe(null, callback);
                }
            }
        }
    }
}
