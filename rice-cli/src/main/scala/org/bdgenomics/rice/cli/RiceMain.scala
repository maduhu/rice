/**
 * Licensed to Big Data Genomics (BDG) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The BDG licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bdgenomics.rice.cli

import java.util.logging.Level._
import org.apache.spark.Logging
import org.bdgenomics.utils.cli._
import scala.collection.mutable.ListBuffer

object RiceMain extends Logging {

  case class CommandGroup(name: String, commands: List[BDGCommandCompanion])

  private val commandGroups =
    List(
      CommandGroup(
        "QUANTIFICATION",
        List(
          Index,
          Quantify)))

  private def printCommands() {
    println("\n")
    println("\nChoose one of the following commands:")
    commandGroups.foreach { grp =>
      println("\n%s".format(grp.name))
      grp.commands.foreach(cmd =>
        println("%20s : %s".format(cmd.commandName, cmd.commandDescription)))
    }
    println("\n")
  }

  def main(args: Array[String]) {
    log.info("rice invoked with args: %s".format(argsToString(args)))
    if (args.size < 1) {
      printCommands()
    } else {

      val commands =
        for {
          grp <- commandGroups
          cmd <- grp.commands
        } yield cmd

      commands.find(_.commandName == args(0)) match {
        case None      => printCommands()
        case Some(cmd) => cmd.apply(args drop 1).run()
      }
    }
  }

  // Attempts to format the `args` array into a string in a way
  // suitable for copying and pasting back into the shell.
  private def argsToString(args: Array[String]): String = {
    def escapeArg(s: String) = "\"" + s.replaceAll("\\\"", "\\\\\"") + "\""
    args.map(escapeArg).mkString(" ")
  }
}
