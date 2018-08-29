pragma solidity ^0.4.24;

import "./ISingularWallet.sol";
import "./ISingular.sol";

/**
ownership history enumeration. 

Another implementation may keep the record on a unique contract account.

@author Bing Ran<bran@udap.io>
*/
interface ITransferHistory {

    /**
     * To get the number of ownership changes of this token.
     * @return the number of ownership records. The first record is the token genesis
     * record.
     */
    function numOfTransfers() view public returns (uint256);
    /**
     * To get a specific transfer record in the format defined by implementation.
     * @param index the inde of the inquired record. It must in the range of
     * [0, numberOfTransfers())
     */
    function getTransferAt(uint256 index) view public returns(bytes);

    /**
     * get all the transfer records in a serialized form that is defined by
     * implementation.
     */
    function getTransferHistory() view public returns (bytes);
}