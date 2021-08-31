import "restriction_account/restriction_account_types.cats"
import "transaction.cats"

# binary layout for an account operation restriction transaction
struct AccountOperationRestrictionTransactionBody
	# account restriction flags
	restrictionFlags = AccountRestrictionFlags

	# number of account restriction additions
	restrictionAdditionsCount = uint8

	# number of account restriction deletions
	restrictionDeletionsCount = uint8

	# reserved padding to align restrictionAdditions on 8-byte boundary
	accountRestrictionTransactionBody_Reserved1 = uint32

	# account restriction additions
	restrictionAdditions = array(TransactionType, restrictionAdditionsCount)

	# account restriction deletions
	restrictionDeletions = array(TransactionType, restrictionDeletionsCount)

# binary layout for a non-embedded account operation restriction transaction
struct AccountOperationRestrictionTransaction
	transaction_version = make_const(uint8, 1)
	transaction_type = make_const(TransactionType, account_operation_restriction)

	inline Transaction
	inline AccountOperationRestrictionTransactionBody

# binary layout for an embedded account operation restriction transaction
struct EmbeddedAccountOperationRestrictionTransaction
	transaction_version = make_const(uint8, 1)
	transaction_type = make_const(TransactionType, account_operation_restriction)

	inline EmbeddedTransaction
	inline AccountOperationRestrictionTransactionBody
