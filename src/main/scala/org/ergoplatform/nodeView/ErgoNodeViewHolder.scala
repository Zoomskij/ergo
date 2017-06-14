package org.ergoplatform.nodeView

import org.ergoplatform.modifiers.block.{ErgoBlock, ErgoHeader, ErgoHeaderSerializer}
import org.ergoplatform.modifiers.transaction.proposition.AnyoneCanSpendProposition
import org.ergoplatform.modifiers.transaction.{AnyoneCanSpendTransaction, AnyoneCanSpendTransactionSerializer}
import org.ergoplatform.nodeView.history.{ErgoHistory, ErgoSyncInfo}
import org.ergoplatform.nodeView.mempool.ErgoMemPool
import org.ergoplatform.nodeView.state.ErgoState
import org.ergoplatform.nodeView.wallet.ErgoWallet
import scorex.core.NodeViewModifier.ModifierTypeId
import scorex.core.serialization.Serializer
import scorex.core.settings.Settings
import scorex.core.transaction.Transaction
import scorex.core.{NodeViewHolder, NodeViewModifier}


class ErgoNodeViewHolder(settings: Settings) extends NodeViewHolder[AnyoneCanSpendProposition,
  AnyoneCanSpendTransaction,
  ErgoBlock] {
  override val networkChunkSize: Int = settings.networkChunkSize

  override type SI = ErgoSyncInfo

  override type HIS = ErgoHistory
  override type MS = ErgoState
  override type VL = ErgoWallet
  override type MP = ErgoMemPool

  override val modifierCompanions: Map[ModifierTypeId, Serializer[_ <: NodeViewModifier]] =
    Map(ErgoHeader.ModifierTypeId -> ErgoHeaderSerializer,
      Transaction.ModifierTypeId -> AnyoneCanSpendTransactionSerializer)

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    reason.printStackTrace()
    System.exit(100) // this actor shouldn't be restarted at all so kill the whole app if that happened
  }

  /**
    * Hard-coded initial view all the honest nodes in a network are making progress from.
    */
  override protected def genesisState: (HIS, MS, VL, MP) = ???

  /**
    * Restore a local view during a node startup. If no any stored view found
    * (e.g. if it is a first launch of a node) None is to be returned
    */
  override def restoreState(): Option[(HIS, MS, VL, MP)] = ???
}
