package dk.kb.metadata.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dk.kb.metadata.utils.ExceptionUtils;

/**
 * Contains the different selectors for the PREMIS metadata, some will translate the value into the 
 * corresponding in the enumerator.
 * 
 * The different selectors will through 'IllegalStateException' if the given value 
 * cannot be found within their enumerator.
 */
public final class PremisPreservationLevelEnumeratorSelector {
    /** Private constructor for this Utility class.*/
    private PremisPreservationLevelEnumeratorSelector() {}

    // Different values for the 'premis:preservationLevelValue'.
    /** Bit safety level: bitSafetyMax*/
    private static final String MAX_BIT_SAFETY = "bitSafetyMax";
    /** Bit safety level: bitSafetyVeryHigh*/
    private static final String VERY_HIGH_BIT_SAFETY = "bitSafetyVeryHigh";
    /** Bit safety level: bitSafetyHigh*/
    private static final String HIGH_BIT_SAFETY = "bitSafetyHigh";
    /** Bit safety level: bitSafetyMedium*/
    private static final String MEDIUM_BIT_SAFETY = "bitSafetyMedium";
    /** Bit safety level: bitSafetyLow*/
    private static final String LOW_BIT_SAFETY = "bitSafetyLow";
    /** Bit safety level: bitSafetyVeryLow*/
    private static final String VERY_LOW_BIT_SAFETY = "bitSafetyVeryLow";
    /** Bit safety level: bitSafetyMin*/
    private static final String MIN_BIT_SAFETY = "bitSafetyMin";

    /** The restricted values for 'premis:preservationLevelValue'.*/
    private static final Set<String> BIT_PRESERVATION_LEVEL_VALUES = new HashSet<String>(Arrays.asList(
            MAX_BIT_SAFETY,
            VERY_HIGH_BIT_SAFETY,
            HIGH_BIT_SAFETY,
            MEDIUM_BIT_SAFETY,
            LOW_BIT_SAFETY,
            VERY_LOW_BIT_SAFETY,
            MIN_BIT_SAFETY));

    /**
     * Retrieves the valid value for the field 'premis:preservationLevelValue'.
     * @param level The value to evaluate.
     * @return The value or the corresponding value which is allowed by the restrictions.
     */
    public static String getBitPreservationLevelValue(String level) {
        if(BIT_PRESERVATION_LEVEL_VALUES.contains(level)) {
            return level;
        }

        // Conversion of old values
        if(level.equals("Aktiv bit bevaring")) {
            return VERY_HIGH_BIT_SAFETY;
        }

        IllegalStateException res = new IllegalStateException("Cannot handle the PreservationLevelValue: '" + level 
                + "'. Only accepts: " + BIT_PRESERVATION_LEVEL_VALUES);
        ExceptionUtils.insertException(res);
        throw res;
    }

    /** Logical preservation strategy: logicalStrategyMigration */
    private static final String MIGRATION_STRATEGY = "logicalStrategyMigration";
    /** Logical preservation strategy: logicalStrategyEmulation */
    private static final String EMULATION_STRATEGY = "logicalStrategyEmulation";
    /** Logical preservation strategy: logicalStrategyVirtualization */
    private static final String VIRTUALIZATION_STRATEGY = "logicalStrategyVirtualization";
    /** Logical preservation strategy: logicalStrategyTechnical */
    private static final String TECHNICAL_STRATEGY = "logicalStrategyTechnical";

    /** The possible values for the logical preservation strategies.*/
    private static final Set<String> LOGICAL_PRESERVATION_LEVEL_VALUES = new HashSet<String>(Arrays.asList(
            MIGRATION_STRATEGY,
            EMULATION_STRATEGY,
            VIRTUALIZATION_STRATEGY,
            TECHNICAL_STRATEGY));

    /**
     * Retrieves the valid value for the field 'premis:preservationLevelValue' for the logical preservation type.
     * @param level The level for the logical preservation to validate.
     * @return The value or the corresponding value which is allowed by the restrictions.
     */
    public static String getLogicalPreservationLevelValue(String level) {
        if(LOGICAL_PRESERVATION_LEVEL_VALUES.contains(level)) {
            return level;
        }

        // Conversion of old values
        if(level.equals("Aktiv bit bevaring")) {
            return MIGRATION_STRATEGY;
        }

        IllegalStateException res = new IllegalStateException("Cannot handle the PreservationLevelValue for logical preservation: '" + level 
                + "'. Only accepts: " + LOGICAL_PRESERVATION_LEVEL_VALUES);
        ExceptionUtils.insertException(res);
        throw res;
    }

    /** Confidentiality level: confidentialityMax */
    private static final String MAX_CONFIDENTIALITY = "confidentialityMax";
    /** Confidentiality level: confidentialityVeryHigh */
    private static final String VERY_HIGH_CONFIDENTIALITY = "confidentialityVeryHigh";
    /** Confidentiality level: confidentialityHigh */
    private static final String HIGH_CONFIDENTIALITY = "confidentialityHigh";
    /** Confidentiality level: confidentialityMedium */
    private static final String MEDIUM_CONFIDENTIALITY = "confidentialityMedium";
    /** Confidentiality level: confidentialityLow */
    private static final String LOW_CONFIDENTIALITY = "confidentialityLow";
    /** Confidentiality level: confidentialityVeryLow */
    private static final String VERY_LOW_CONFIDENTIALITY = "confidentialityVeryLow";
    /** Confidentiality level: confidentialityMin */
    private static final String MIN_CONFIDENTIALITY = "confidentialityMin";

    /** The values for the confidentiality.*/
    private static final Set<String> CONFIDENTIALITY_PRESERVATION_LEVEL_VALUES = new HashSet<String>(Arrays.asList(
            MAX_CONFIDENTIALITY,
            VERY_HIGH_CONFIDENTIALITY,
            HIGH_CONFIDENTIALITY,
            MEDIUM_CONFIDENTIALITY,
            LOW_CONFIDENTIALITY,
            VERY_LOW_CONFIDENTIALITY,
            MIN_CONFIDENTIALITY));

    /**
     * Retrieves the valid value for the field 'premis:preservationLevelValue' for the confidentiality.
     * @param level The value to evaluate.
     * @return The value or the corresponding value which is allowed by the restrictions.
     */
    public static String getConfidentialityPreservationLevelValue(String level) {
        if(CONFIDENTIALITY_PRESERVATION_LEVEL_VALUES.contains(level)) {
            return level;
        }

        // Conversion of old values
        if(level.equals("Aktiv bit bevaring")) {
            return LOW_CONFIDENTIALITY;
        }

        IllegalStateException res = new IllegalStateException("Cannot handle the PreservationLevelValue for confidentiality: '" + level 
                + "'. Only accepts: " + CONFIDENTIALITY_PRESERVATION_LEVEL_VALUES);
        ExceptionUtils.insertException(res);
        throw res;
    }
}
