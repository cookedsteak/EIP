package org.web3j.solidity.gradle.plugin

import org.gradle.api.Project

import javax.inject.Inject
/**
 * Extension for Solidity compilation options.
 * bran: deprecated in favor of configuring directly with the task class
 * @deprecated
 */
class SolidityExtension {

    static final NAME = 'solidity'

    private Project project

    private Boolean overwrite

    private Boolean optimize

    private Integer optimizeRuns

    private Boolean prettyJson

    private OutputComponent[] outputComponents

    public String[] srcMaps

    ////

    public String generatedJavaPackageName;

    public String generatedFilesBaseDir

    public Boolean useNativeJavaTypes;

    public List<String> excludedContracts;


    @Inject
    SolidityExtension(final Project project) {
        this.project = project
        this.optimize = true
        this.overwrite = true
        this.prettyJson = false
        this.optimizeRuns = 0
        this.outputComponents = [OutputComponent.BIN, OutputComponent.ABI]
    }

    boolean getOptimize() {
        return optimize
    }

    void setOptimize(final boolean optimize) {
        this.optimize = optimize
    }

    int getOptimizeRuns() {
        return optimizeRuns
    }

    void setOptimizeRuns(final int optimizeRuns) {
        this.optimizeRuns = optimizeRuns
    }

    boolean getPrettyJson() {
        return prettyJson
    }

    void setPrettyJson(final boolean prettyJson) {
        this.prettyJson = prettyJson
    }

    boolean getOverwrite() {
        return overwrite
    }

    void setOverwrite(final boolean overwrite) {
        this.overwrite = overwrite
    }

    OutputComponent[] getOutputComponents() {
        return outputComponents
    }

    void setOutputComponents(final OutputComponent[] outputComponents) {
        this.outputComponents = outputComponents
    }

}
