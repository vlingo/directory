// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.directory.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.actors.Startable;
import io.vlingo.actors.Stoppable;
import io.vlingo.cluster.model.attribute.AttributesProtocol;
import io.vlingo.wire.multicast.Group;
import io.vlingo.wire.node.Node;

public interface DirectoryService extends Startable, Stoppable {

  public static DirectoryService instance(final Stage stage, final Node localNode) {

    final Network network = new Network(
            new Group(Properties.instance.directoryGroupAddress(), Properties.instance.directoryGroupPort()),
            Properties.instance.directoryIncomingPort());

    final int maxMessageSize = Properties.instance.directoryMessageBufferSize();

    final Timing timing = new Timing(Properties.instance.directoryMessageProcessingInterval(),
            Properties.instance.directoryMessagePublishingInterval());

    final int unpublishedNotifications = Properties.instance.directoryUnregisteredServiceNotifications();

    final DirectoryService directoryService = DirectoryService.instance(stage, localNode, network, maxMessageSize,
            timing, unpublishedNotifications);

    return directoryService;
  }

  public static DirectoryService instance(final Stage stage, final Node localNode, final Network network,
          final int maxMessageSize, final Timing timing, final int unpublishedNotifications) {

    final Definition definition = Definition.has(DirectoryServiceActor.class,
            Definition.parameters(localNode, network, maxMessageSize, timing, unpublishedNotifications),
            "vlingo-directory-service");

    return stage.actorFor(DirectoryService.class, definition);
  }

  public void assignLeadership();

  public void relinquishLeadership();

  public void use(final AttributesProtocol client);

  public static class Network {
    public final Group publisherGroup;
    public final int incomingPort;

    public Network(final Group publisherGroup, final int incomingPort) {
      this.publisherGroup = publisherGroup;
      this.incomingPort = incomingPort;
    }
  }

  public static class Timing {
    public final int processingInterval;
    public final int publishingInterval;

    public Timing(final int processingInterval, final int publishingInterval) {
      this.processingInterval = processingInterval;
      this.publishingInterval = publishingInterval;
    }
  }
}
