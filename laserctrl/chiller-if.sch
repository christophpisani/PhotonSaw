EESchema Schematic File Version 2  date 2011-11-09T19:03:07 CET
LIBS:power
LIBS:device
LIBS:transistors
LIBS:conn
LIBS:linear
LIBS:regul
LIBS:74xx
LIBS:cmos4000
LIBS:adc-dac
LIBS:memory
LIBS:xilinx
LIBS:special
LIBS:microcontrollers
LIBS:dsp
LIBS:microchip
LIBS:analog_switches
LIBS:motorola
LIBS:texas
LIBS:intel
LIBS:audio
LIBS:interface
LIBS:digital-audio
LIBS:philips
LIBS:display
LIBS:cypress
LIBS:siliconi
LIBS:opto
LIBS:contrib
LIBS:valves
LIBS:mounting
LIBS:23k256
LIBS:tps78233
LIBS:drv8811
LIBS:mcu-nxp
LIBS:opto-transistor-4p2
LIBS:atmega328p-a
LIBS:atmel
LIBS:microsd
EELAYER 25  0
EELAYER END
$Descr A4 11700 8267
encoding utf-8
Sheet 3 12
Title ""
Date "9 nov 2011"
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
Text Notes 1800 2400 0    50   ~ 0
Note: wire 4 sits on the 3.3V logic ground,\n5,6,7 sits on unregulated 24V ground.
Connection ~ 2050 2200
Wire Wire Line
	2950 2200 1900 2200
Wire Wire Line
	2950 2000 2900 2000
Wire Wire Line
	2950 2200 2950 2000
Wire Wire Line
	2050 2200 2050 2100
Wire Wire Line
	2050 2100 2100 2100
Wire Wire Line
	2100 1900 1900 1900
Wire Wire Line
	1900 1900 1900 1950
Wire Wire Line
	3100 1800 3100 1850
Wire Wire Line
	2900 1800 3100 1800
Wire Wire Line
	2100 1700 1800 1700
Wire Wire Line
	2900 1700 3100 1700
Wire Wire Line
	3100 1700 3100 1650
Wire Wire Line
	1800 1800 2100 1800
Wire Wire Line
	2900 1900 2950 1900
Wire Wire Line
	2950 1900 2950 1800
Connection ~ 2950 1800
Wire Wire Line
	2100 2000 2050 2000
Wire Wire Line
	2050 2000 2050 1900
Connection ~ 2050 1900
Wire Wire Line
	2900 2100 2950 2100
Connection ~ 2950 2100
$Comp
L +24V #PWR19
U 1 1 4EB6DCEF
P 1900 2200
F 0 "#PWR19" H 1900 2150 20  0001 C CNN
F 1 "+24V" H 1900 2300 30  0000 C CNN
	1    1900 2200
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR18
U 1 1 4EB6DCC0
P 1900 1950
F 0 "#PWR18" H 1900 1950 30  0001 C CNN
F 1 "GND" H 1900 1880 30  0001 C CNN
	1    1900 1950
	1    0    0    -1  
$EndComp
Text HLabel 1800 1800 0    50   Output ~ 0
RXD
Text HLabel 1800 1700 0    50   Input ~ 0
TXD
$Comp
L +3.3V #PWR16
U 1 1 4EB6DB66
P 3100 1650
F 0 "#PWR16" H 3100 1610 30  0001 C CNN
F 1 "+3.3V" H 3100 1760 30  0000 C CNN
	1    3100 1650
	1    0    0    -1  
$EndComp
$Comp
L GND #PWR17
U 1 1 4EB6DB2E
P 3100 1850
F 0 "#PWR17" H 3100 1850 30  0001 C CNN
F 1 "GND" H 3100 1780 30  0001 C CNN
	1    3100 1850
	1    0    0    -1  
$EndComp
$Comp
L CONN_5X2 P4
U 1 1 4EB6DB27
P 2500 1900
F 0 "P4" H 2500 2200 60  0000 C CNN
F 1 "Chiller" V 2500 1900 50  0000 C CNN
	1    2500 1900
	1    0    0    -1  
$EndComp
$EndSCHEMATC
