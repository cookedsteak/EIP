pragma solidity ^0.4.24;

import "../contracts/ISingular.sol";
import "../Implementation2/SingularWallet.sol";

interface IStateChanelFactory {
    function newChannel();

}

/**
A member of a state channel.
*/
interface IChannelMember{
}

contract ChannelMemeber is SingularWallet, IChannelMember {
    IStateChannel public theChannel;
    function attachToChannel(
        IStateChannel channel
    )
    external public {
        theChannel = channel;
    }
}



interface ISignedStatement {

}


interface IStateChannel is IChannelMember{

    /**
    to add a member to the channel
    */
    function addMember(
        ChannelMemeber member
    ) external;

    /**
    To list all the members of this channel
    */
    function allMembers() view external returns(
        ChannelMember[]
    );

    function allAssets()
    view external returns (
        ISingular[]     ///< all assets in this channel from all members.
    );


    /**

    */
    function update(ISignedStatement statement) external;

    /**
    to request to close the channel by all participants.
    */
    function closeByAll (
        ISignedStatement statement,     ///< the state to submit, can be NULL state
        ISignature[] allSignatures      ///< all signatures.
    ) external;


    /**
    @title request to close the channel by some participants
    @dev channel will notify the other participants of the request and grant grace period. This
    may happen when some of the participant are not cooperative in settlement (e.g. intentionally
    delaying settlement, or been offline for extended period of time), or some of the
    participants wants to exit the state channel with a version of state that is not the latest
    version but is a favorable state to the proposers. The latter situation is an attack from some
    of the participants to the other participants. The channel MUST grant a chance in a grace period
    for the other parties to prevent the pre-mature exit by submitting a later version of state, which
    may or may not be the final state, by way of invoking various update and close methods.
    */

    function requestToCloseBySome (
        ISignedStatement statement,     ///< the state to submit, can be NULL state
        ISignature[] someSignatures     ///< signatures from those who want to force close
    ) external;

    /**
    a participant offers to swap one of his items in the channel with another item that belongs to someone
    in the SAME channel.
    */
    function offerToSwap(
        ISingular owned,        ///< an item to be offered out that is in the statechannel and belongs
                                ///< to the msg.sender.
        ISingular wanted,       ///< an item in the statechannel that belongs to the other participant
                                ///< can be address(0), meaning to give an item to the other party for free.
        uint validTill,         ///< how long the offer is valid for
        string note             ///< additional information
    )
    external
    returns(
        uint offerId            ///< the id of the offer that must be referenced later in accepting/rejecting
                                ///< an offer
    );

    /**
    a participant offers to swap one of his items in the channel with another item that belongs to another
    ADJACENT channel. The two channels MUST share a common member, which means they are connected by the member.

    The connecting member is critical since he/she has the knowledge of the latest state of both channels
    and can decide to bridge the transactions.

    Implementation ideas:

    - find the connecting member.
    - This channel time-lock the local token for the desire token and for the connecting member.
    - the connecting member makes another time-locked swap offer, in the target channel, from the previous swap
    offer to the desired item, to its owner in the other channel
    - The other party in the other channel would `accept` the offer to settle the cross-channel transaction, in a
    separate agreement. Claims in two channels are reconciled.
    //TODO Internally both channel states will be updated??
    - some steps later, a joined closure of the two channels can be done to settle all transactions.

    */
    function offerToSwapAcrossChannel(
        ISingular owned,        ///< an item to be offered out that is in the state channel and belongs
                                ///< to the msg.sender.
        ISingular wanted,       ///< an item in the state channel that belongs to the other participant
                                ///< can be address(0), meaning to give an item to the other party for free.
        IStateChannel targetC,  ///< the channel where the desired item is contained. The channel MUST be an
                                ///< adjacent channel to this one via a joined member.
        uint validTill,         ///< how long the offer is valid for
        string note             ///< additional information
    )
    external
    returns(
        uint offerId            ///< the id of the offer that must be referenced later in accepting/rejecting
    ///< an offer
    );



    /**
    To accept a previously set offer.
    */
    function acceptOffer(
        uint offerId,           ///< the offer reference to a previous offer
        ISingular owned         ///< an item to be offered out that is in the statechannel and belongs
                                ///< to the msg.sender. Can be address(0) to accept a give-away offer.
    )
    external
    returns(
        bool success,           ///< true if success, false otherwise
        string reason           ///< additional info
    );



}

