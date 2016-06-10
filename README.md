This package implements generic CRC calculations up to 64 bits wide.
It aims to be fairly fast and fairly complete, allowing users to match pretty much
any CRC algorithm used in the wild by choosing appropriate Parameters. This obviously 
includes all popular CRC algorithms, such as CRC64-ISO, CRC64-ECMA, CRC32, CRC32C, CRC16, CCITT, XMODEM and many others.
See http://reveng.sourceforge.net/crc-catalogue/ for a good list of CRC algorithms and their parameters.

This package has been largely inspired by Ross Williams' 1993 paper "A Painless Guide to CRC Error Detection Algorithms".


## Usage

Using src is easy. Here is an example of calculating CCITT crc.
```java
import com.github.snksoft.crc.CRC;

public class Example
{
    public static void main(String [] args) {
        String data = "123456789";
        long ccittCrc = CRC.calculateCRC(CRC.Parameters.CCITT, data.getBytes());
        System.out.printf("CRC is 0x%04X\n", ccittCrc); // prints "CRC is 0x29B1"

    }
}
```

For larger data, table driven implementation is faster. Here is how to use it.
```java
import com.github.snksoft.crc.CRC;

public class Example
{
    public static void main(String [] args) {
        String data = "123456789";
       	CRC tableDriven = new CRC(CRC.Parameters.XMODEM);
       	long xmodemCrc = tableDriven.calculateCRC(data.getBytes());
        System.out.printf("CRC is 0x%04X\n", xmodemCrc); // prints "CRC is 0x31C3"

       	// You can also reuse CRC object instance for another crc calculation.
        // Given that the only state for a CRC calculation is the "intermediate value"
        // and it is stored in your code, you can even use same CRC instance to calculate CRC
        // of multiple data sets in parallel.
       	// And if data is too big, you may feed it in chunks
       	long curValue = tableDriven.init(); // initialize intermediate value
       	curValue = tableDriven.update(curValue, "123456789".getBytes()); // feed first chunk
        curValue = tableDriven.update(curValue, "01234567890".getBytes()); // feed next chunk
       	long xmodemCrc2 = tableDriven.finalCRC(curValue); // gets CRC of whole data ("12345678901234567890")
        System.out.printf("CRC is 0x%04X\n", xmodemCrc2); // prints "CRC is 0x2C89"
    }
}
```
