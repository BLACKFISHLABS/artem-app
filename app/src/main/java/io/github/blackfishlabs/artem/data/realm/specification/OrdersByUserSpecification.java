package io.github.blackfishlabs.artem.data.realm.specification;

import io.github.blackfishlabs.artem.data.realm.interfaces.RealmResultsSpecification;
import io.github.blackfishlabs.artem.domain.OrderStatus;
import io.github.blackfishlabs.artem.domain.entity.OrderEntity;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.Sort.ASCENDING;
import static io.realm.Sort.DESCENDING;

public class OrdersByUserSpecification implements RealmResultsSpecification<OrderEntity> {

    private final String mCompanyId;
    private long mInitialIssueDate = 0;
    private long mFinalIssueDate = 0;
    private boolean mOrderedByIssueDate = false;
    private boolean mOrderedByCustomerName = false;

    private int status = OrderStatusSpecificationFilter.NONE;

    public OrdersByUserSpecification(final String companyId) {
        mCompanyId = companyId;
    }

    public OrdersByUserSpecification byIssueDate(
            final long initialIssueDate, final long finalIssueDate) {
        mInitialIssueDate = initialIssueDate;
        mFinalIssueDate = finalIssueDate;
        return this;
    }

    public OrdersByUserSpecification byStatus(@OrderStatusSpecificationFilter int status) {
        this.status = status;
        return this;
    }

    public OrdersByUserSpecification orderByIssueDate() {
        mOrderedByIssueDate = true;
        mOrderedByCustomerName = false;
        return this;
    }

    public OrdersByUserSpecification orderByCustomerName() {
        mOrderedByCustomerName = true;
        mOrderedByIssueDate = false;
        return this;
    }

    @Override
    public RealmResults<OrderEntity> toRealmResults(final Realm realm) {
        RealmQuery<OrderEntity> query = createQuery(realm);

        switch (status) {
            case OrderStatusSpecificationFilter.NOT_CANCELLED: {
                query.not().equalTo(OrderEntity.Fields.STATUS, OrderStatus.STATUS_CANCELLED);
                break;
            }
            case OrderStatusSpecificationFilter.CREATED_OR_MODIFIED: {
                query.beginGroup()
                        .equalTo(OrderEntity.Fields.STATUS, OrderStatus.STATUS_CREATED)
                        .or()
                        .equalTo(OrderEntity.Fields.STATUS, OrderStatus.STATUS_MODIFIED)
                        .endGroup();
                break;
            }
            case OrderStatusSpecificationFilter.INVOICED: {
                query.equalTo(OrderEntity.Fields.STATUS, OrderStatus.STATUS_INVOICED);
                break;
            }
            case OrderStatusSpecificationFilter.CANCELLED: {
                query.equalTo(OrderEntity.Fields.STATUS, OrderStatus.STATUS_CANCELLED);
                break;
            }
        }

        ifByIssueDate(query);

        return findAll(query);
    }

    private RealmQuery<OrderEntity> createQuery(final Realm realm) {
        return realm
                .where(OrderEntity.class)
                .equalTo(OrderEntity.Fields.OWNER, mCompanyId);
    }

    private void ifByIssueDate(RealmQuery<OrderEntity> query) {
        if (mInitialIssueDate != 0 && mFinalIssueDate != 0) {
            query
                    .greaterThanOrEqualTo(OrderEntity.Fields.ISSUE_DATE, mInitialIssueDate)
                    .lessThanOrEqualTo(OrderEntity.Fields.ISSUE_DATE, mFinalIssueDate);
        }
    }

    private RealmResults<OrderEntity> findAll(RealmQuery<OrderEntity> query) {
        if (mOrderedByIssueDate) {
            return query.sort(OrderEntity.Fields.ISSUE_DATE, DESCENDING).findAll();
        } else if (mOrderedByCustomerName) {
            return query.sort(OrderEntity.Fields.CUSTOMER_NAME, ASCENDING).findAll();
        } else {
            return query.findAll();
        }
    }
}