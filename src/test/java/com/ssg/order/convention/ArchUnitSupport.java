package com.ssg.order.convention;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;

public abstract class ArchUnitSupport {
    protected final String ROOT_PACKAGE = "com.ssg";
    protected final String MODULE_PACKAGE = ROOT_PACKAGE + ".order";

    protected final JavaClasses IMPORTED_CLASSES = new ClassFileImporter()
        .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
        .withImportOption(Predefined.DO_NOT_INCLUDE_ARCHIVES)
        .withImportOption(Predefined.DO_NOT_INCLUDE_JARS)
        .importPackages(MODULE_PACKAGE);
}
