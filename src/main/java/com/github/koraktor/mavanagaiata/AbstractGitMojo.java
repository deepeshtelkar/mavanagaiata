/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * This abstract Mojo implements initializing a JGit Repository and provides
 * this Repository instance to subclasses.
 *
 * @author Sebastian Staudt
 * @see Repository
 */
public abstract class AbstractGitMojo extends AbstractMojo {

    /**
     * The project base directory
     *
     * @parameter expression="${basedir}/.git"
     * @required
     */
    protected File gitDir;

    /**
     * The Maven project
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    protected Repository repository;

    /**
     * Saves a property with the given name into the project's properties
     *
     * The value will be stored two times – with "mavanagaiata" and "mvngit" as
     * a prefix.
     *
     * @param name The property name
     * @param value The value of the property
     */
    protected void addProperty(String name, Object value) {
        Properties properties = this.project.getProperties();
        properties.put("mavanagaiata." + name, value);
        properties.put("mvngit." + name, value);
    }

    /**
     * Initializes a JGit Repository object for further reference
     *
     * @see Repository
     * @throws MojoExecutionException if retrieving information from the Git
     *         repository fails
     */
    protected void initRepository() throws MojoExecutionException {
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            this.repository = repositoryBuilder
                .setGitDir(this.gitDir)
                .readEnvironment()
                .findGitDir()
                .build();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read Git repository", e);
        }
    }

    /**
     * Returns a commit object for the repository's current HEAD
     *
     * @return The commit object of the repository's current HEAD
     * @see RevCommit
     * @throws IOException if the repository HEAD could not be retrieved
     */
    protected RevCommit getHead() throws IOException, MojoExecutionException {
        if(this.repository == null) {
            this.initRepository();
        }

        RevWalk revWalk = new RevWalk(this.repository);
        ObjectId head = this.repository.getRef("HEAD").getObjectId();
        return revWalk.parseCommit(head);
    }

    /**
     * Sets the Git directory used to initialize the repository
     *
     * @param gitDir The Git directory of the repository
     * @see #initRepository()
     */
    protected void setGitDir(File gitDir) {
        this.gitDir = gitDir;
    }

    /**
     * The Maven project in which context this Mojo is executed
     *
     * @param project The current Maven project
     */
    protected void setProject(MavenProject project) {
        this.project = project;
    }

}