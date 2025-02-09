/*
 * (c) Copyright 2021 Mark Elliot. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markelliot.gradle.versions;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;

public final class UpdateVersionsPlugin implements Plugin<Project> {
    public static final String NEW_VERSIONS = "newVersions";

    @Override
    public void apply(Project project) {
        TaskProvider<CheckNewVersionsTask> checkNewVersions =
                project.getTasks()
                        .register(
                                "checkNewVersions",
                                CheckNewVersionsTask.class,
                                task -> {
                                    task.getReportFile()
                                            .set(
                                                    project.getLayout()
                                                            .getBuildDirectory()
                                                            .file("versions-report.yml"));
                                    task.setDescription(
                                            "Checks for and reports on existence of newer versions of dependencies and plugins");
                                });

        createOutgoingConfiguration(project, checkNewVersions);
    }

    private static void createOutgoingConfiguration(
            Project project, TaskProvider<CheckNewVersionsTask> task) {
        Configuration outgoingConfiguration =
                project.getConfigurations()
                        .create(
                                NEW_VERSIONS,
                                conf -> {
                                    conf.setCanBeResolved(false);
                                    conf.setCanBeConsumed(true);
                                    conf.setVisible(true);
                                });

        project.getArtifacts()
                .add(
                        outgoingConfiguration.getName(),
                        task.flatMap(CheckNewVersionsTask::getReportFile),
                        artifact -> artifact.builtBy(task));
    }
}
